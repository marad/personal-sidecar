package gh.marad.sidecar.obsidianvault.flatnote.internal

import gh.marad.sidecar.obsidianvault.flatnote.Block
import gh.marad.sidecar.obsidianvault.flatnote.Line

internal class FlatnoteRenderer {
    fun render(blocks: List<Block>): String {
       val sb = StringBuilder()
        blocks.forEach {
            renderBlock(it, sb)
        }
        return sb.toString().trimEnd()
    }

    private fun renderBlock(it: Block, sb: StringBuilder) = when(it) {
        is Block.Text -> renderTextBlock(it, sb)
        is Block.Empty -> renderEmptyBlock(it, sb)
        is Block.Code -> renderCodeBlock(it, sb)
        is Block.Quote -> renderQuoteBlock(it, sb)
        is Block.List -> renderListBlock(it, sb)
        is Block.Header -> renderHeaderBlock(it, sb)
        is Block.Frontmatter -> renderFrontmatterBlock(it, sb)
        is Block.NonEmpty<*> -> TODO()
    }

    private fun renderTextBlock(it: Block.Text, sb: StringBuilder) {
        renderLinesInNonEmptyTextBlock(it, sb)
    }

    private fun renderEmptyBlock(it: Block.Empty, sb: StringBuilder) {
        repeat(it.emptyLineCount) {
            sb.appendLine()
        }
    }

    private fun renderCodeBlock(it: Block.Code, sb: StringBuilder) {
        sb.appendLine("```${it.language ?: ""}")
        renderLinesInNonEmptyTextBlock(it, sb)
        sb.appendLine("```")
    }

    private fun renderQuoteBlock(it: Block.Quote, sb: StringBuilder) {
        it.lines.forEach {
            sb.append("> ")
            sb.appendLine(it.content)
        }
    }

    private fun renderListBlock(it: Block.List, sb: StringBuilder) {
        it.lines.forEach {
            sb.append(" ".repeat(it.indent))
            sb.append(it.bullet)
            sb.append(" ")
            sb.appendLine(it.content)
        }
    }

    private fun renderHeaderBlock(it: Block.Header, sb: StringBuilder) {
        sb.append("#".repeat(it.header.level))
        sb.append(" ")
        sb.appendLine(it.header.content)
    }

    private fun renderFrontmatterBlock(it: Block.Frontmatter, sb: StringBuilder) {
        sb.appendLine("---")
        renderLinesInNonEmptyTextBlock(it, sb)
        sb.appendLine("---")
    }

    private fun renderLinesInNonEmptyTextBlock(it: Block.NonEmpty<Line.Text>, sb: StringBuilder) {
        it.lines.forEach {
            renderTextLine(sb, it)
        }
    }

    private fun renderTextLine(sb: StringBuilder, it: Line.Text) {
        sb.append(" ".repeat(it.indent))
        sb.appendLine(it.content)
    }
}