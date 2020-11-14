package gh.marad.sidecar.obsidian.vault

import gh.marad.sidecar.obsidian.flatnote.Block
import gh.marad.sidecar.obsidian.flatnote.FlatnoteConfig
import gh.marad.sidecar.obsidian.flatnote.Line
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant


class Inbox internal constructor(private val inbox: Path) {
    init {
        ensureInboxIsMarkdownFile()
        ensureInboxIsNotAtRootLevel()
        makeInboxIfNotExists()
    }

    fun updateContent(updateFunc: InboxModifier.() -> Unit) {
        val flatnote = FlatnoteConfig().createFlatnote()
        val inboxContent = InboxModifier(flatnote.parse(Files.readAllLines(inbox)))
        updateFunc.invoke(inboxContent)
        val markdown = flatnote.render(inboxContent.blocks())
        Files.write(inbox, markdown.toByteArray())
    }

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

class InboxModifier internal constructor(fileBlocks: List<Block>) {
    private val tagName = "#task"
    private val blocks = fileBlocks.toMutableList()
    internal fun blocks(): List<Block> = blocks

    init {
        ensureHasFrontmatter()
        ensureHasTextBlock()
    }

    fun lastUpdateTime(): Long = (frontmatter()["lastUpdate"]?.toLong() ?: Instant.now().epochSecond)
    fun setLastUpdateTime(timeToSet: Long) {
        val updatedFrontmatter = frontmatter().also { it["lastUpdate"] = timeToSet.toString() }
        updateFrontmatter(updatedFrontmatter)
    }

    fun appendNote(note: String) {
        appendItem(note)
        markForClearing()
    }

    fun appendUrl(url: String, comment: String? = null) {
        appendItem(if (comment != null) "$url - $comment" else url)
        markForClearing()
    }

    private fun appendItem(item: String) {
        blocks.add(Block.List(listOf(Line.ListItem(item, "-", indent = 0))))
    }

    private fun markForClearing() {
        blocks.removeAt(1)
        blocks.add(1, Block.Text(listOf(Line.Text(tagName, indent = 0))))
    }

    private fun frontmatter(): MutableMap<String, String> {
        ensureHasFrontmatter()
        val frontmatter = blocks.first() as Block.Frontmatter
        return frontmatter.properties.toMutableMap()
    }

    private fun updateFrontmatter(new: Map<String, String>) {
        ensureHasFrontmatter()
        blocks[0] = Block.Frontmatter(new)
    }

    private fun ensureHasFrontmatter() {
        if (blocks.first() !is Block.Frontmatter) {
            blocks.add(0, Block.Frontmatter(mapOf()))
        }
    }

    private fun ensureHasTextBlock() {
        if (blocks[1] !is Block.Text) {
            blocks.add(1, Block.Text(listOf(Line.Text("", indent = 0))))
        }
    }
}
