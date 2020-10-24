package flatnote.renderer

import gh.marad.sidecar.obsidianvault.flatnote.Block
import gh.marad.sidecar.obsidianvault.flatnote.internal.FlatnoteRenderer
import gh.marad.sidecar.obsidianvault.flatnote.Line
import kotlin.test.Test
import kotlin.test.expect

class FlatnoteRendererSpec {
    private fun renderBlocks(blocks: List<Block>): String {
        return FlatnoteRenderer().render(blocks)
    }

    private fun renderSingleBlock(block: Block): String {
        return renderBlocks(listOf(block))
    }

    @Test
    fun `should render simple text block`() {
        expect("""
            text 1
              text 2
        """.trimIndent()) {
            renderSingleBlock(Block.Text(listOf(
                    Line.Text("text 1", indent = 0),
                    Line.Text("text 2", indent = 2),
            )))
        }
    }

    @Test
    fun `should render code block`() {
        expect("""
            ```
            code 1
              code 2
            ```
        """.trimIndent()) {
            renderSingleBlock(Block.Code(listOf(
                    Line.Text("code 1", indent = 0),
                    Line.Text("code 2", indent = 2),
            ), language = null))
        }

        expect("""
            ```python
            python code
            ```
        """.trimIndent()) {
            renderSingleBlock(Block.Code(listOf(
                    Line.Text("python code", indent = 0),
            ), language = "python"))
        }
    }

    @Test
    fun `should render quote block`() {
        expect("""
            > quote 1
            > quote 2
        """.trimIndent()) {
            renderSingleBlock(Block.Quote(listOf(
                    Line.Quote("quote 1"),
                    Line.Quote("quote 2")
            )))
        }
    }

    @Test
    fun `should render list block`() {
        expect("""
            - list 1
              * list 2
        """.trimIndent()) {
            renderSingleBlock(Block.List(listOf(
                    Line.ListItem("list 1", bullet = "-", indent = 0),
                    Line.ListItem("list 2", bullet = "*", indent = 2),
            )))
        }
    }

    @Test
    fun `should render header block`() {
        expect("""
            # header
        """.trimIndent()) {
            renderSingleBlock(Block.Header(Line.Header("header", level = 1)))
        }


        expect("""
            ## header
        """.trimIndent()) {
            renderSingleBlock(Block.Header(Line.Header("header", level = 2)))
        }
    }

    @Test
    fun `should render frontmatter block`() {
        expect("""
            ---
            property: value
            ---
        """.trimIndent()) {
            renderSingleBlock(Block.Frontmatter(mapOf("property" to "value")))
        }
    }

    @Test
    fun `should render multiple blocks`() {
        expect("""
            # header
            text
            - list
            > quote
            
            
            ```
            code
            ```
        """.trimIndent()) {
            renderBlocks(listOf(
                    Block.Header(Line.Header("header", level = 1)),
                    Block.Text(listOf(Line.Text("text", indent = 0))),
                    Block.List(listOf(Line.ListItem("list", bullet = "-", indent = 0))),
                    Block.Quote(listOf(Line.Quote("quote"))),
                    Block.Empty(2),
                    Block.Code(listOf(Line.Text("code", indent = 0)), language = null)
            ))
        }
    }
}
