package gh.marad.sidecar.obsidianvault

import gh.marad.sidecar.obsidianvault.app.Configuration
import gh.marad.sidecar.obsidianvault.app.DailyNote
import gh.marad.sidecar.obsidianvault.app.Inbox
import org.osgi.service.cm.ConfigurationAdmin
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.slf4j.LoggerFactory
import java.lang.RuntimeException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate


@Component(service = [ObsidianVault::class], immediate = true)
class FilesystemObsidianVault : ObsidianVault {
    @Reference
    private lateinit var configAdmin: ConfigurationAdmin
    private lateinit var config: Configuration
    private val log = LoggerFactory.getLogger(FilesystemObsidianVault::class.java)

    @Activate
    fun setup() {
        val configuration = configAdmin.getConfiguration(Constants.CONFIG_PID)
        val vaultPath = (configuration.properties["path"]
                ?: throw RuntimeException("Missing path to obsidian vault")) as String
        config = Configuration(Paths.get(vaultPath))
        log.info("Started obsidian vault with path $vaultPath")
    }

    override fun dailyNoteExists(day: LocalDate): Boolean {
        val dailyNote = DailyNote(config.vaultPath, day)
        return dailyNote.exists()
    }

    override fun createDailyNoteFromTemplate(day: LocalDate) {
        val dailyNote = DailyNote(config.vaultPath, day)
        dailyNote.create()
    }

    override fun appendNoteToInbox(note: String) {
        Inbox(config.inboxPath).apply {
            appendNote(note)
            markForClearing()
        }
    }

    override fun appendUrlToInbox(url: String, comment: String?) {
        Inbox(config.inboxPath).apply {
            appendUrl(url, comment)
            markForClearing()
        }
    }

    override fun readAllLines(notePath: String): List<String> {
        return Files.readAllLines(config.vaultPath.resolve(notePath))
    }
}