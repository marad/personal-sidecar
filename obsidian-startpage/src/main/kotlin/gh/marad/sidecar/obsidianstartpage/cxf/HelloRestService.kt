package gh.marad.sidecar.obsidianstartpage.cxf

import gh.marad.sidecar.obsidianvault.ObsidianVault
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

data class MyResponse(val title: String, val number: Int)

@Produces(MediaType.APPLICATION_JSON)
class HelloService {

    lateinit var obsidianVault: ObsidianVault

    @GET
    @Path("/hello")
    fun hello(): MyResponse {
        obsidianVault.readAllLines("Factorio 1.0.md")
        return MyResponse("Some title", 23)
    }
}
