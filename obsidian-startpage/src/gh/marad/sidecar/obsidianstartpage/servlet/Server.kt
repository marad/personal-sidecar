package gh.marad.sidecar.obsidianstartpage.servlet

import gh.marad.sidecar.obsidianvault.ObsidianVault
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.component.annotations.ServiceScope
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern
import javax.servlet.Servlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component(
        service = [Servlet::class],
        scope = ServiceScope.PROTOTYPE,
        property = [
            "alias=/startpage",
            "servlet-name=Startpage",
        ],
)
@HttpWhiteboardServletPattern("/startpage")
class Server : HttpServlet(), Servlet {

    val log = org.slf4j.LoggerFactory.getLogger(Server::class.java)

    @Reference
    lateinit var obsidianVault: ObsidianVault

    lateinit var router: Router

    @Activate
    fun setup() {
        router = Router().apply {
            get("/style.css") { _, res ->
                res.contentType = "text/css"
                res.writer.write("""@media print{*,:after,:before{background:0 0!important;color:#000!important;box-shadow:none!important;text-shadow:none!important}a,a:visited{text-decoration:underline}a[href]:after{content:" (" attr(href) ")"}abbr[title]:after{content:" (" attr(title) ")"}a[href^="#"]:after,a[href^="javascript:"]:after{content:""}blockquote,pre{border:1px solid #999;page-break-inside:avoid}thead{display:table-header-group}img,tr{page-break-inside:avoid}img{max-width:100%!important}h2,h3,p{orphans:3;widows:3}h2,h3{page-break-after:avoid}}@media screen and (min-width:32rem) and (max-width:48rem){html{font-size:15px}}@media screen and (min-width:48rem){html{font-size:16px}}body{line-height:1.85}.splendor-p,p{font-size:1rem;margin-bottom:1.3rem}.splendor-h1,.splendor-h2,.splendor-h3,.splendor-h4,h1,h2,h3,h4{margin:1.414rem 0 .5rem;font-weight:inherit;line-height:1.42}.splendor-h1,h1{margin-top:0;font-size:3.998rem}.splendor-h2,h2{font-size:2.827rem}.splendor-h3,h3{font-size:1.999rem}.splendor-h4,h4{font-size:1.414rem}.splendor-h5,h5{font-size:1.121rem}.splendor-h6,h6{font-size:.88rem}.splendor-small,small{font-size:.707em}canvas,iframe,img,select,svg,textarea,video{max-width:100%}@import url(http://fonts.googleapis.com/css?family=Merriweather:300italic,300);html{font-size:18px;max-width:100%}body{color:#444;font-family:Merriweather,Georgia,serif;margin:0;max-width:100%}:not(div):not(img):not(body):not(html):not(li):not(blockquote):not(p),p{margin:1rem auto;max-width:36rem;padding:.25rem}div,div img{width:100%}blockquote p{font-size:1.5rem;font-style:italic;margin:1rem auto;max-width:48rem}li{margin-left:2rem}h1{padding:4rem 0!important}p{color:#555;height:auto;line-height:1.45}code,pre{font-family:Menlo,Monaco,"Courier New",monospace}pre{background-color:#fafafa;font-size:.8rem;overflow-x:scroll;padding:1.125em}a,a:visited{color:#3498db}a:active,a:focus,a:hover{color:#2980b9}""")
                res.writer.close()
            }

            get("/*") { req, res ->
                res.contentType = "text/html"

                val writer = res.writer
                val startpage = obsidianVault.readAllLines("startpage${req.pathInfo}.md").joinToString(separator = System.lineSeparator())
                val parser = Parser.builder().build()
                val document = parser.parse(startpage)
                val renderer = HtmlRenderer.builder().build()
                val html = renderer.render(document)

                log.error(document.toString())
                writer.appendHTML().html {
                    head {
//                script(src="https://cdn.jsdelivr.net/npm/jquery@3.3.1/dist/jquery.min.js") {}
//                script(src="https://cdn.jsdelivr.net/npm/fomantic-ui@2.8.6/dist/semantic.min.js") {}
//                link(rel="stylesheet", type = "text/css", href = "https://cdn.jsdelivr.net/npm/fomantic-ui@2.8.6/dist/semantic.min.css")
                        link(rel="stylesheet", type = "text/css", href = "style.css")

                        title("")
                    }
                    body {
                        unsafe {
                            raw(html)
                        }
                    }
                }
//        resp.writer.appendHTML().html {
//            body {
//                h1 {
//                    text()
//                }
//            }
//        }
                writer.close()
            }
        }
    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val handler = router.findHandler(req.toRouteRequest())
        handler(req, resp)
    }
}
