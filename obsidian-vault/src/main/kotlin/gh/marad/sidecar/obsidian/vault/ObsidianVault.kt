package gh.marad.sidecar.obsidian.vault

import java.time.LocalDate

interface ObsidianVault {
    fun dailyNoteExists(day: LocalDate = LocalDate.now()): Boolean
    fun createDailyNoteFromTemplate(day: LocalDate = LocalDate.now())
    fun inbox(): Inbox
    fun readAllLines(notePath: String): List<String>
    fun overwriteContents(notePath: String, lines: List<String>) {
        overwriteContents(notePath, lines.joinToString(separator = "\n"))
    }
    fun overwriteContents(notePath: String, contents: String)
}