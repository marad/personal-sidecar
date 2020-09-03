package gh.marad.sidecar.obsidianstartpage

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

enum class HttpMethod {
    GET, POST, PUT, DELETE,
    HEAD, OPTIONS, TRACE, PATCH
}

typealias Handler = (request: HttpServletRequest, response: HttpServletResponse) -> Unit

data class RequestPath(private val path: String) {
    override fun toString() = path
    fun matchesTo(routePath: RoutePath): Boolean {
        val regex = routePath.toRegex()
        return regex.matches(path)
    }
}

data class RouteRequest(val method: HttpMethod, val path: RequestPath)

data class RoutePath(private val path: String) {
    override fun toString() = path
    fun toRegex(): Regex = path.replace("*", "[^/]+").toRegex()
}
data class Route(
        val method: HttpMethod,
        val path: RoutePath,
        val handler: Handler
) {
    fun satisfies(request: RouteRequest): Boolean {
        return request.method == method &&
                request.path.matchesTo(path)
    }
}

class Router {
    private val routes = mutableListOf<Route>()
    private var notFoundHandler: Handler = {_, response ->
        response.addHeader("content-type", "text/plain")
        response.writer.apply {
            write("Not found")
            close()
        }
    }

    fun get(path: String, handler: Handler) = routes.add(Route(HttpMethod.GET, RoutePath(path), handler))
    fun post(path: String, handler: Handler) = routes.add(Route(HttpMethod.POST, RoutePath(path), handler))

    fun notFound(handler: Handler) {
        notFoundHandler = handler
    }

    fun findHandler(request: RouteRequest): Handler {
        return routes.firstOrNull { it.satisfies(request) }
                ?.handler
                ?: notFoundHandler
    }
}


fun HttpServletRequest.toRouteRequest() = RouteRequest(
        method = HttpMethod.valueOf(this.method.toUpperCase()),
        path = RequestPath(this.pathInfo)
)