package gh.marad.sidecar.obsidian.vault.infra

import gh.marad.sidecar.obsidian.Constants
import gh.marad.sidecar.obsidian.vault.Inbox
import gh.marad.sidecar.obsidian.vault.internal.Configuration
import gh.marad.sidecar.obsidian.vault.ObsidianVault
import gh.marad.sidecar.obsidian.vault.internal.FilesystemObsidianVault
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

    override fun inbox(): Inbox = proxed.inbox()

    override fun readAllLines(notePath: String): List<String> =
            proxed.readAllLines(notePath)

    override fun overwriteContents(notePath: String, contents: String) =
            proxed.overwriteContents(notePath, contents)
}