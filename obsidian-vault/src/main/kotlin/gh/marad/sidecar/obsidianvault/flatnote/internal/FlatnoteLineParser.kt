package gh.marad.sidecar.obsidianvault.flatnote.internal

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

private val HeaderRegex = "^#+\\s+.*".toRegex()
private val QuoteRegex = "^>.*".toRegex()
private val ListItemRegex = "^\\s*[-*]\\s+.*".toRegex()

class FlatnoteLineParser {

    fun parseLines(content: List<String>): List<Line> {
        return content.map {
            when {
                it.isEmpty() -> Line.Empty
                it.matches(ListItemRegex) -> parseListItem(it)
                it.matches(HeaderRegex) -> parseHeader(it)
                it.matches(QuoteRegex) -> parseQuote(it)
                else -> parseText(it)
            }
        }
    }

    private fun parseListItem(line: String): Line {
        val indent = calcIndent(line)
        val (bullet, content) = splitByFirstWhitespace(line.trim())
        return Line.ListItem(content.trim(), bullet.trim(), indent)
    }

    private fun parseHeader(line: String): Line {
        val (header, content) = splitByFirstWhitespace(line)
        return Line.Header(content.trim(), header.length)
    }

    private fun parseQuote(line: String): Line {
        val content = line.drop(1).trim()
        return Line.Quote(content)
    }

    private fun parseText(line: String): Line {
        val indentSize = calcIndent(line)
        return Line.Text(line.trim(), indent = indentSize)
    }

    private fun splitByFirstWhitespace(line: String) = line.split("\\s+".toRegex(), limit = 2)

    private fun calcIndent(line: String): Int {
        return line.takeWhile { it.isWhitespace() }.length
    }
}
