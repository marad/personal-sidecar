package flatnote.lines

import gh.marad.sidecar.obsidianvault.flatnote.internal.Line
import kotlin.test.Test
import kotlin.test.expect

class FlatnoteListLineParsingSpec {
    @Test
    fun `should parse list`() {
        expect(Line.ListItem("item", bullet = "-", indent = 0)) { parseSingleLine("- item") }
        expect(Line.ListItem("item", bullet = "-", indent = 2)) { parseSingleLine("  - item") }
        expect(Line.ListItem("item", bullet = "*", indent = 0)) { parseSingleLine("* item") }
        expect(Line.ListItem("item", bullet = "*", indent = 2)) { parseSingleLine("  * item") }
    }

    @Test
    fun `should trim list item contents`() {
        expect(Line.ListItem("item", bullet = "-", indent = 2)) { parseSingleLine("  -     item    ") }
        expect(Line.ListItem("item", bullet = "*", indent = 2)) { parseSingleLine("  *     item    ") }
    }
}