package flatnote.lines

import gh.marad.sidecar.obsidian.flatnote.internal.FlatnoteLineParser
import gh.marad.sidecar.obsidian.flatnote.Line

fun parseSingleLine(line: String): Line {
    return FlatnoteLineParser().parseLines(listOf(line)).first()
}

fun parseMultipleLines(lines: List<String>): List<Line> {
    return FlatnoteLineParser().parseLines(lines)
}