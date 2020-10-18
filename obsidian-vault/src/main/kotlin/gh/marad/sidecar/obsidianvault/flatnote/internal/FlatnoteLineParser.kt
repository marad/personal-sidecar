package gh.marad.sidecar.obsidianvault.flatnote.internal

import gh.marad.sidecar.obsidianvault.flatnote.Line

private val HeaderRegex = "^#+\\s+.*".toRegex()
private val QuoteRegex = "^>.*".toRegex()
private val ListItemRegex = "^\\s*[-*]\\s+.*".toRegex()

internal class FlatnoteLineParser {

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
