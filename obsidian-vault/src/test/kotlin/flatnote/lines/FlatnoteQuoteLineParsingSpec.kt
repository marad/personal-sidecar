package flatnote.lines

import gh.marad.sidecar.obsidian.flatnote.Line
import kotlin.test.Test
import kotlin.test.expect

class FlatnoteQuoteLineParsingSpec {
    @Test
    fun `should parse the quote`() {
        expect(Line.Quote("quote")) { parseSingleLine("> quote")}
    }

    @Test
    fun `should trim the contents`() {
        expect(Line.Quote("quote")) { parseSingleLine(">   quote   ")}
    }
}