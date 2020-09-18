import com.google.common.jimfs.Configuration as FsConfig
import com.google.common.jimfs.Jimfs
import gh.marad.sidecar.obsidianvault.FilesystemObsidianVault
import gh.marad.sidecar.obsidianvault.ObsidianVault
import gh.marad.sidecar.obsidianvault.app.Configuration
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.expect

class InboxSpec {
    private val nl = System.lineSeparator()!!
    private lateinit var fs: FileSystem
    private lateinit var inboxPath: Path
    private lateinit var vault: ObsidianVault

    @BeforeTest
    fun setup() {
        fs = Jimfs.newFileSystem(FsConfig.unix())
        val secondOfApril = LocalDate.of(2020, 4, 2)
        val config = Configuration(
                vaultPath = fs.getPath("/vault"),
                date = secondOfApril)
        vault = FilesystemObsidianVault(config)
        inboxPath = config.inboxPath
        Files.createDirectories(inboxPath.parent)
    }

    @Test
    fun `should append url without comment`() {
        // given
        setInboxContent("- existing")
        // when
        vault.appendUrlToInbox("http://github.com", null)
        // then
        expectToBeMarkedForClearing()
        expectThatInboxContainsContent(
                "- existing$nl- http://github.com")
    }

    @Test
    fun `should append url with comment`() {
        // given
        setInboxContent("- existing")
        // when
        vault.appendUrlToInbox("http://github.com", "Fun VCS")
        // then
        expectToBeMarkedForClearing()
        expectThatInboxContainsContent(
                "- existing$nl- http://github.com - Fun VCS")
    }

    @Test
    fun `should append note`() {
        // given
        setInboxContent("- existing")
        // when
        vault.appendNoteToInbox("Some note")
        // then
        expectToBeMarkedForClearing()
        expectThatInboxContainsContent("- existing$nl- Some note")
    }

    private fun expectToBeMarkedForClearing() {
        expectThatInboxContainsContent("#task Wyczyścić inbox")
    }

    private fun expectThatInboxContainsContent(expectedContent: String) {
        expect(true) {
            val content = String(Files.readAllBytes(inboxPath)).trim()
            content.contains(expectedContent)
        }
    }

    private fun setInboxContent(content: String) {
        Files.createDirectories(inboxPath.parent)
        Files.createFile(inboxPath)
        Files.write(inboxPath, content.toByteArray())
    }
}
