package flatnote.lines

import gh.marad.sidecar.obsidian.flatnote.Line
import kotlin.test.Test
import kotlin.test.expect

class FlatnoteTextLineParsingSpec {
    @Test
    fun `should parse simple text line`() {
        expect(Line.Text("line", indent = 0)) { parseSingleLine("line") }
    }

    @Test
    fun `should read line indent and trim the line`() {
        expect(Line.Text("line", indent = 2)) { parseSingleLine("\t line \t")}
    }
}