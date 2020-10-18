package gh.marad.sidecar.obsidianvault.flatnote.internal

import java.lang.RuntimeException

sealed class Block {
    data class Empty(val emptyLineCount: Int) : Block()
    abstract class NonEmpty<T : Line> : Block() {
        abstract val lines: kotlin.collections.List<T>
    }
    data class Text(override val lines: kotlin.collections.List<Line.Text>) : NonEmpty<Line.Text>()
    data class List(override val lines: kotlin.collections.List<Line.ListItem>) : NonEmpty<Line.ListItem>()
    data class Header(val header: Line.Header) : NonEmpty<Line.Header>() {
        override val lines = listOf(header)
    }
    data class Quote(override val lines: kotlin.collections.List<Line.Quote>) : NonEmpty<Line.Quote>()
    data class Code(override val lines: kotlin.collections.List<Line.Text>) : NonEmpty<Line.Text>()
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

    class CodeBlockBuilder : BlockBuilder() {
        private val lines = mutableListOf<Line.Text>()
        private var acceptModeLines = true
        override fun build(): Block = Block.Code(lines)
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
}

class FlatnoteBlockParser {
    fun parseBlocks(lines: List<Line>): List<Block> {
        val blocks = mutableListOf<Block>()

        var currentBlockBuilder: BlockBuilder? = null

        lines.forEach { line ->
            if (currentBlockBuilder == null) {
                currentBlockBuilder = createBlockBuilder(line)
            } else {
                if (currentBlockBuilder!!.canAddLine(line)) {
                    currentBlockBuilder!!.addLine(line)
                } else {
                    blocks.add(currentBlockBuilder!!.build())
                    currentBlockBuilder = createBlockBuilder(line)
                }
            }
        }

        currentBlockBuilder?.let {
            blocks.add(it.build())
        }

        return blocks
    }

    private fun createBlockBuilder(line: Line): BlockBuilder {
        return when(line) {
            is Line.Empty -> BlockBuilder.EmptyBlockBuilder()
            is Line.Text -> {
                if (line.content.startsWith("```")) {
                    BlockBuilder.CodeBlockBuilder()
                } else {
                    BlockBuilder.TextBlockBuilder(line)
                }
            }
            is Line.ListItem -> BlockBuilder.ListBlockBuilder(line)
            is Line.Header -> BlockBuilder.HeaderBlockBuilder(line)
            is Line.Quote -> BlockBuilder.QuoteBlockBuilder(line)
            is Line.NonEmpty -> throw RuntimeException("Invalid block")
        }
    }
}

