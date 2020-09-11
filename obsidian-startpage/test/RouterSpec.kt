import gh.marad.sidecar.obsidianstartpage.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.test.Test
import kotlin.test.expect

class RouterSpec {

    private fun String.asGetRequest() = RouteRequest(HttpMethod.GET, RequestPath(this))

    private fun makeHandler(name: String) = object : Handler {
        override fun invoke(request: HttpServletRequest, response: HttpServletResponse) {}
        override fun toString(): String = name
    }

    private val testHandler = makeHandler("Test Handler")
    private val notFoundHandler = makeHandler("Not found handler")

    @Test
    fun `should find handler by path`() {
        val router = Router().apply {
            get("/test", testHandler)
            notFound(notFoundHandler)
        }

        expect(testHandler, "Simple get failed") {
            router.findHandler("/test".asGetRequest())
        }

        expect(notFoundHandler, "Not found failed") {
            router.findHandler("/not-defined-path".asGetRequest())
        }
    }

    @Test
    fun `should check request method`() {
        val router = Router().apply {
            post("/test", testHandler)
            notFound(notFoundHandler)
        }

        expect(notFoundHandler) {
            router.findHandler("/test".asGetRequest())
        }
    }

    @Test
    fun `should find single wildcard routes`() {
        val router = Router().apply {
            get("/some/*/path", testHandler)
            notFound(notFoundHandler)
        }

        expect(testHandler, "Invalid handler for `other`") {
            router.findHandler("/some/other/path".asGetRequest())
        }
        expect(testHandler, "Invalid handler for `different`") {
            router.findHandler("/some/different/path".asGetRequest())
        }

        expect(notFoundHandler, "Invalid handler for multi element path") {
            router.findHandler("/some/multiple/element/path".asGetRequest())
        }
    }

    @Test
    fun `should work with multiple wildcard notes`() {
        val router = Router().apply {
            get("/some/*/path/*", testHandler)
            notFound(notFoundHandler)
        }

        expect(testHandler) {
            router.findHandler("/some/other/path/again".asGetRequest())
        }
    }

    @Test
    fun `should look for routes in same order they were defined`() {
        val firstHandler = makeHandler("First Handler")
        val secondHandler = makeHandler("Second Handler")
        val wildcardHandler = makeHandler("Wildcard Handler")
        val thirdHandler = makeHandler("Third Handler")
        val router = Router().apply {
            get("/test/route", firstHandler)
            get("/test/route", secondHandler)
            get("/*/route", wildcardHandler)
            get("/third/route", thirdHandler)
            notFound(notFoundHandler)
        }

        expect(firstHandler) { router.findHandler("/test/route".asGetRequest()) }
        expect(wildcardHandler) { router.findHandler("/other/route".asGetRequest()) }
        expect(wildcardHandler) { router.findHandler("/third/route".asGetRequest()) }
        expect(notFoundHandler) { router.findHandler("/something/else".asGetRequest()) }

    }
}