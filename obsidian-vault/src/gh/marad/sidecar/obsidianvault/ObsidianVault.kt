package gh.marad.sidecar.obsidianvault

import java.time.LocalDate

interface ObsidianVault {
    fun dailyNoteExists(day: LocalDate = LocalDate.now()): Boolean
    fun createDailyNoteFromTemplate(day: LocalDate = LocalDate.now())
    fun appendNoteToInbox(note: String)
    fun appendUrlToInbox(url: String, comment: String? = null)
    fun readAllLines(notePath: String): List<String>
}