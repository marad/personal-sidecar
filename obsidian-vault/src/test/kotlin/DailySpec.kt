import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import gh.marad.sidecar.obsidianvault.app.DailyNote
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.expect

class DailySpec {
    @Test
    fun `creates daily note from day template`() {
        // given
        val fs = Jimfs.newFileSystem(Configuration.unix())
        createFile(fs.getPath("/vault/daily/template/thursday.md"), "template content")
        val vaultPath = fs.getPath("/vault")
        val dailyNote = DailyNote(vaultPath, LocalDate.of(2020, 7, 2))

        // when
        dailyNote.create()

        // then
        expect("template content") {
            String(Files.readAllBytes(fs.getPath("/vault/daily/2020-07-02.md")))
        }
    }

    @Test
    fun `uses default template when there is no daily template`() {
        // given
        val fs = Jimfs.newFileSystem(Configuration.unix())
        createFile(fs.getPath("/vault/daily/template/default.md"), "default content")
        val vaultPath = fs.getPath("/vault")
        val dailyNote = DailyNote(vaultPath, LocalDate.of(2020, 7, 2))

        // when
        dailyNote.create()

        // then
        expect("default content") {
            String(Files.readAllBytes(fs.getPath("/vault/daily/2020-07-02.md")))
        }
    }

    @Test
    fun `creates empty note when there is no daily nor default template`() {
        // given
        val fs = Jimfs.newFileSystem(Configuration.unix())
        val vaultPath = fs.getPath("/vault")
        val dailyNote = DailyNote(vaultPath, LocalDate.of(2020, 7, 2))

        // when
        dailyNote.create()

        // then
        expect("") {
            String(Files.readAllBytes(fs.getPath("/vault/daily/2020-07-02.md")))
        }
    }

    @Test
    fun `does not overwrite existing daily note`() {
        // given
        val fs = Jimfs.newFileSystem(Configuration.unix())
        createFile(fs.getPath("/vault/daily/template/thursday.md"), "template content")
        createFile(fs.getPath("/vault/daily/2020-07-02.md"), "existing daily note")
        val vaultPath = fs.getPath("/vault")
        val dailyNote = DailyNote(vaultPath, LocalDate.of(2020, 7, 2))

        // when
        dailyNote.create()

        // then
        expect("existing daily note") {
            String(Files.readAllBytes(fs.getPath("/vault/daily/2020-07-02.md")))
        }
    }

    private fun createFile(file: Path, content: String) {
        Files.createDirectories(file.parent)
        Files.createFile(file)
        Files.write(file, content.toByteArray())
    }
}
