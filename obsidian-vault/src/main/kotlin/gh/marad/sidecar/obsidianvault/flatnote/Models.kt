package gh.marad.sidecar.obsidianvault.flatnote

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
    data class Code(override val lines: kotlin.collections.List<Line.Text>, val language: String?): NonEmpty<Line.Text>()
}

sealed class Line {
    object Empty : Line()
    abstract class NonEmpty : Line() {
        abstract val content: String
    }
    data class Text(override val content: String, val indent: Int) : NonEmpty()
    data class ListItem(override val content: String, val bullet: String, val indent: Int) : NonEmpty()
    data class Header(override val content: String, val level: Int) : NonEmpty()
    data class Quote(override val content: String) : NonEmpty()
}

