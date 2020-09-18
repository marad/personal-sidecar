import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import gh.marad.sidecar.obsidianvault.app.Inbox
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.BeforeTest
import kotlin.test.expect

class InboxSpec {
    private val nl = System.lineSeparator()!!
    private lateinit var fs: FileSystem
    private lateinit var inboxPath: Path

    @BeforeTest
    fun setup() {
        fs = Jimfs.newFileSystem(Configuration.unix())
        inboxPath = fs.getPath("/vault/inbox.md")
        Files.createDirectories(inboxPath.parent)
    }

    @Test
    fun `should append url without comment`() {
        Files.write(inboxPath, "- existing".toByteArray())
        Inbox(inboxPath).appendUrl("http://github.com", null)
        expect("- existing$nl- http://github.com") {
            String(Files.readAllBytes(inboxPath)).trim()
        }
    }

    @Test
    fun `should append url with comment`() {
        Files.write(inboxPath, "- existing".toByteArray())
        Inbox(inboxPath).appendUrl("http://github.com", "Fun VCS")
        expect("- existing$nl- http://github.com - Fun VCS") {
            String(Files.readAllBytes(inboxPath)).trim()
        }
    }

    @Test
    fun `should append note`() {
        Files.write(inboxPath, "- existing".toByteArray())
        Inbox(inboxPath).appendNote("Some note")
        expect("- existing$nl- Some note") {
            String(Files.readAllBytes(inboxPath)).trim()
        }
    }
}