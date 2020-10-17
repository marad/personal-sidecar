package gh.marad.sidecar.obsidianvault

import gh.marad.sidecar.obsidianvault.app.Configuration
import org.osgi.service.cm.ConfigurationAdmin
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import java.lang.RuntimeException
import java.nio.file.Paths
import java.time.LocalDate

@Component(service = [ObsidianVault::class], immediate = true)
class ObsidianVaultServiceProxy : ObsidianVault {
    @Reference
    private lateinit var configAdmin: ConfigurationAdmin
    private lateinit var proxed: ObsidianVault

    @Activate
    fun setup() {
        val configuration = configAdmin.getConfiguration(Constants.CONFIG_PID)
        val vaultPath = (configuration.properties["path"]
                ?: throw RuntimeException("Missing path to obsidian vault")) as String
        proxed = FilesystemObsidianVault(Configuration(Paths.get(vaultPath)))
    }

    override fun dailyNoteExists(day: LocalDate)
            = proxed.dailyNoteExists(day)
    override fun createDailyNoteFromTemplate(day: LocalDate)
            = proxed.createDailyNoteFromTemplate(day)

    override fun appendNoteToInbox(note: String)
            = proxed.appendNoteToInbox(note)

    override fun appendUrlToInbox(url: String, comment: String?)
            = proxed.appendUrlToInbox(url, comment)

    override fun readAllLines(notePath: String): List<String> =
            proxed.readAllLines(notePath)

    override fun overwriteContents(notePath: String, contents: String) =
            proxed.overwriteContents(notePath, contents)
}