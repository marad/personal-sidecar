import com.google.common.jimfs.Configuration as FsConfig
import com.google.common.jimfs.Jimfs
import gh.marad.sidecar.obsidianvault.FilesystemObsidianVault
import gh.marad.sidecar.obsidianvault.ObsidianVault
import gh.marad.sidecar.obsidianvault.app.Configuration
import java.nio.file.FileSystem
import java.nio.file.Files
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

class DailySpec {
    private lateinit var fs: FileSystem
    private lateinit var vault: ObsidianVault
    private val secondOfApril = LocalDate.of(2020, 4, 2)

    @BeforeTest
    fun setup() {
        fs = Jimfs.newFileSystem(FsConfig.unix())
        vault = FilesystemObsidianVault(
                Configuration(
                        vaultPath = fs.getPath("/vault"),
                        date = secondOfApril))
    }

    @Test
    fun `checks if daily note exits`() {
        // given
        createFile("/vault/daily/2020-04-02.md", "irrelevant")

        // expect
        expect(true) { vault.dailyNoteExists(day = secondOfApril) }
        expect(false) { vault.dailyNoteExists(day = LocalDate.MIN) }
    }


    @Test
    fun `creates daily note from day template`() {
        // given
        createFile("/vault/daily/template/thursday.md", "template content")

        // when
        vault.createDailyNoteFromTemplate(day = secondOfApril)

        // then
        expect("template content") {
            String(Files.readAllBytes(fs.getPath("/vault/daily/2020-04-02.md")))
        }
    }

    @Test
    fun `uses default template when there is no daily template`() {
        // given
        createFile("/vault/daily/template/default.md", "default content")

        // when
        vault.createDailyNoteFromTemplate(day = secondOfApril)

        // then
        expect("default content") {
            String(Files.readAllBytes(fs.getPath("/vault/daily/2020-04-02.md")))
        }
    }

    @Test
    fun `creates empty note when there is no daily nor default template`() {
        // when
        vault.createDailyNoteFromTemplate(day = secondOfApril)

        // then
        expect("") {
            String(Files.readAllBytes(fs.getPath("/vault/daily/2020-04-02.md")))
        }
    }

    @Test
    fun `does not overwrite existing daily note`() {
        // given
        createFile("/vault/daily/template/thursday.md", "template content")
        createFile("/vault/daily/2020-04-02.md", "existing daily note")

        // when
        vault.createDailyNoteFromTemplate(day = secondOfApril)

        // then
        expect("existing daily note") {
            String(Files.readAllBytes(fs.getPath("/vault/daily/2020-04-02.md")))
        }
    }

    private fun createFile(filePath: String, content: String) {
        val file = fs.getPath(filePath)
        Files.createDirectories(file.parent)
        Files.createFile(file)
        Files.write(file, content.toByteArray())
    }
}
