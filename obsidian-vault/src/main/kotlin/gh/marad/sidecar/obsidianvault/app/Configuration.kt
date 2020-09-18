package gh.marad.sidecar.obsidianvault.app

import java.nio.file.Path
import java.time.LocalDate

internal data class Configuration(
    val vaultPath: Path,
    val date: LocalDate = LocalDate.now()
) {
    private val inboxIcon = "\uD83D\uDCE7"
    val inboxPath = vaultPath.resolve("$inboxIcon Inbox.md")
}