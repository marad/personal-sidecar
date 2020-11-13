package gh.marad.sidecar.obsidian.flatnote.internal

import gh.marad.sidecar.obsidian.flatnote.Block
import gh.marad.sidecar.obsidian.flatnote.Line
import java.lang.RuntimeException

internal class FlatnoteBlockParser {
    fun parseBlocks(lines: List<Line>): List<Block> {
        val blocks = mutableListOf<Block>()

        var currentBlockBuilder: BlockBuilder? = null

        lines.forEach { line ->
            val builder = currentBlockBuilder

            if (builder == null || !builder.canAddLine(line)) {
                builder?.let { blocks.add(it.build()) }
                currentBlockBuilder = createBlockBuilder(line, isFirstLine = blocks.isEmpty())
            } else {
                builder.addLine(line)
            }
        }

        currentBlockBuilder?.let {
            blocks.add(it.build())
        }

        return blocks
    }

    private fun createBlockBuilder(line: Line, isFirstLine: Boolean): BlockBuilder {
        return when(line) {
            is Line.Empty -> BlockBuilder.EmptyBlockBuilder()
            is Line.Text -> {
                when {
                    line.content.startsWith("```") -> {
                        val language = line.content.substring(3).ifBlank { null }
                        BlockBuilder.CodeBlockBuilder(language)
                    }
                    isFirstLine && line.content.startsWith("---") -> {
                        BlockBuilder.FrontmatterBlockBuilder()
                    }
                    else -> {
                        BlockBuilder.TextBlockBuilder(line)
                    }
                }
            }
            is Line.ListItem -> BlockBuilder.ListBlockBuilder(line)
            is Line.Header -> BlockBuilder.HeaderBlockBuilder(line)
            is Line.Quote -> BlockBuilder.QuoteBlockBuilder(line)
            is Line.NonEmpty -> throw RuntimeException("Invalid block")
        }
    }
}

private sealed class BlockBuilder {
    abstract fun build(): Block
    abstract fun canAddLine(line: Line): Boolean
    abstract fun addLine(line: Line)

    class EmptyBlockBuilder(startingEmptyLineCount: Int = 1) : BlockBuilder() {
        private var emptyLineCount = startingEmptyLineCount
        override fun build(): Block = Block.Empty(emptyLineCount)
        override fun canAddLine(line: Line): Boolean = line is Line.Empty
        override fun addLine(line: Line) { emptyLineCount += 1 }
    }

    class TextBlockBuilder(line: Line.Text) : BlockBuilder() {
        private val lines = mutableListOf(line)
        override fun build() = Block.Text(lines)
        override fun canAddLine(line: Line) = line is Line.Text
        override fun addLine(line: Line) { lines.add(line as Line.Text) }
    }

    class ListBlockBuilder(line: Line.ListItem) : BlockBuilder() {
        private val lines = mutableListOf(line)
        override fun build(): Block = Block.List(lines)
        override fun canAddLine(line: Line): Boolean = line is Line.ListItem
        override fun addLine(line: Line) { lines.add(line as Line.ListItem) }
    }

    class HeaderBlockBuilder(val header: Line.Header) : BlockBuilder() {
        override fun build(): Block = Block.Header(header)
        override fun canAddLine(line: Line) = false
        override fun addLine(line: Line) {}
    }

    class QuoteBlockBuilder(quote: Line.Quote) : BlockBuilder() {
        private val lines = mutableListOf(quote)
        override fun build(): Block = Block.Quote(lines)
        override fun canAddLine(line: Line): Boolean = line is Line.Quote
        override fun addLine(line: Line) { lines.add(line as Line.Quote) }
    }

    class CodeBlockBuilder(private val language: String?) : BlockBuilder() {
        private val lines = mutableListOf<Line.Text>()
        private var acceptModeLines = true
        override fun build(): Block = Block.Code(lines, language)
        override fun canAddLine(line: Line): Boolean = acceptModeLines && line is Line.Text
        override fun addLine(line: Line) {
            val codeLine = line as Line.Text
            if (codeLine.content.startsWith("```")) {
                acceptModeLines = false
            } else {
                lines.add(codeLine)
            }
        }
    }

    class FrontmatterBlockBuilder : BlockBuilder() {
        private val lines = mutableListOf<Line.Text>()
        private var acceptLines = true
        override fun build(): Block = Block.Frontmatter(lines.map { readProperty(it.content) }.toMap())
        override fun canAddLine(line: Line): Boolean = acceptLines && line is Line.Text
        override fun addLine(line: Line) {
            val propertyLine = line as Line.Text
            if (propertyLine.content == "---") {
                acceptLines = false
            } else {
                lines.add(propertyLine)
            }
        }

        private fun readProperty(it: String): Pair<String, String> {
            val (key, value) = it.split(FRONTMATTER_PROPERTY_SPLIT_REGEX, limit = 2)
            return Pair(key, value)
        }

        companion object {
            private val FRONTMATTER_PROPERTY_SPLIT_REGEX = "\\s*:\\s*".toRegex()
        }
    }
}

