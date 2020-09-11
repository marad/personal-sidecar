package gh.marad.sidecar.obsidianstartpage.cxf

import org.osgi.service.component.annotations.Component
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Component
@JaxrsResource
@HttpWhiteboardResource(pattern = ["/hello/*"], prefix = "static")
class AnotherService {

    @Path("test")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun hello(): String {
        return "Hello World"
    }

}