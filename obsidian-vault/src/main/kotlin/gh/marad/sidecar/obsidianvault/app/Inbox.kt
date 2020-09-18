package gh.marad.sidecar.obsidianvault.app

import java.nio.file.Files
import java.nio.file.Path

internal class Inbox internal constructor(private val inbox: Path) {
    init {
        ensureInboxIsMarkdownFile()
        ensureInboxIsNotAtRootLevel()
        makeInboxIfNotExists()
    }

    private val tagName = "#task"
    private val clearMessage = "Wyczyścić inbox"

    fun markForClearing() {
        val contents = readLines()
        if(!contents.hasLeadingTaskTag()) {
            contents.apply { add(0, "$tagName $clearMessage") }.save()
        }
    }

    fun removeClearTask() {
        val contents = readLines()
        if(contents.hasLeadingTaskTag()) {
            contents.removeAt(0)
            contents.save()
        }
    }

    fun appendNote(note: String) {
        appendItem(note)
    }

    fun appendUrl(url: String, comment: String?) {
        appendItem(if (comment != null) "$url - $comment" else url)
    }

    private fun String.hasLeadingTaskTag() = this.startsWith(tagName)
    private fun List<String>.hasLeadingTaskTag() = this.firstOrNull()?.hasLeadingTaskTag() ?: false
    private fun appendItem(item: String) { readLines().apply { add("- $item") }.save() }
    private fun readLines(): MutableList<String> = Files.readAllLines(inbox)
    private fun List<String>.save() = Files.write(inbox, this)

    private fun ensureInboxIsMarkdownFile() {
        if(!inbox.toString().endsWith(".md")) {
            throw RuntimeException("$inbox is not a Markdown file")
        }
    }
    private fun ensureInboxIsNotAtRootLevel() {
        if (!Files.isDirectory(inbox.parent)) {
            throw RuntimeException("Invalid inbox location")
        }
    }

    private fun makeInboxIfNotExists() {
        if (Files.notExists(inbox)) {
            Files.createDirectories(inbox.parent)
            Files.createFile(inbox)
        }
    }

}
