package gh.marad.sidecar.obsidian.vault.internal

import java.nio.file.Files
import java.nio.file.Path
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class DailyNote internal constructor(vaultPath: Path, date: LocalDate) {
    private val template =
        DailyTemplate(vaultPath, date)
    private val dailyNoteFileName = DateTimeFormatter.ISO_DATE.format(date)
    private val dailyNote = vaultPath.resolve("daily/$dailyNoteFileName.md")

    fun create() {
        if (!exists()) {
            makeMissingDirectories()
            Files.write(dailyNote, template.content().toByteArray())
        }
    }

    fun exists(): Boolean {
        return Files.exists(dailyNote)
    }

    private fun makeMissingDirectories() {
        if (Files.notExists(dailyNote.parent)) {
            Files.createDirectories(dailyNote.parent)
        }
    }

    private class DailyTemplate(vaultPath: Path, date: LocalDate) {
        private val templateFileName = DayOfWeek.from(date).name.toLowerCase() + ".md"
        private val templateNote = vaultPath.resolve("daily/template/$templateFileName")
        private val defaultNote = vaultPath.resolve("daily/template/default.md")

        fun content(): String = when {
            Files.exists(templateNote) -> {
                String(Files.readAllBytes(templateNote))
            }
            Files.exists(defaultNote) -> {
                String(Files.readAllBytes(defaultNote))
            }
            else -> {
                ""
            }
        }
    }

}

