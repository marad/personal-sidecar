package gh.marad.sidecar.obsidian.vault.internal

import gh.marad.sidecar.obsidian.vault.Inbox
import gh.marad.sidecar.obsidian.vault.ObsidianVault
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.time.LocalDate


internal class FilesystemObsidianVault(private val config: Configuration) : ObsidianVault {
    private val log = LoggerFactory.getLogger(FilesystemObsidianVault::class.java)

    init {
        log.info("Started obsidian vault with path ${config.vaultPath}")
    }

    override fun dailyNoteExists(day: LocalDate): Boolean {
        val dailyNote = DailyNote(config.vaultPath, day)
        return dailyNote.exists()
    }

    override fun createDailyNoteFromTemplate(day: LocalDate) {
        val dailyNote = DailyNote(config.vaultPath, day)
        dailyNote.create()
    }

    override fun inbox(): Inbox = Inbox(config.inboxPath)

    override fun readAllLines(notePath: String): List<String> {
        return Files.readAllLines(config.vaultPath.resolve(notePath))
    }

    override fun overwriteContents(notePath: String, contents: String) {
        Files.write(config.vaultPath.resolve(notePath), contents.toByteArray(charset = Charsets.UTF_8))
    }
}