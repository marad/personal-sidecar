package flatnote.lines

import gh.marad.sidecar.obsidianvault.flatnote.internal.Line
import kotlin.test.Test
import kotlin.test.expect

class FlatnoteHeaderLineParsingSpec {

    @Test
    fun `should read header line`() {
        expect(Line.Header("Title", 1)) { parseSingleLine("# Title") }
        expect(Line.Header("Title", 2)) { parseSingleLine("## Title") }
        expect(Line.Header("Title", 3)) { parseSingleLine("### Title") }
    }

    @Test
    fun `should trim content of header`() {
        expect(Line.Header("Title", 1)) { parseSingleLine("#   Title  ") }
    }
}