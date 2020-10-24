package flatnote.blocks

import gh.marad.sidecar.obsidianvault.flatnote.Block
import gh.marad.sidecar.obsidianvault.flatnote.internal.FlatnoteBlockParser
import gh.marad.sidecar.obsidianvault.flatnote.internal.FlatnoteLineParser
import gh.marad.sidecar.obsidianvault.flatnote.Line
import kotlin.test.Test
import kotlin.test.expect

class FlatnoteBlockParsingSpec {

    private fun parseSingleBlock(content: String): Block {
        return parseBlocks(content).first()
    }

    private fun parseBlocks(content: String): List<Block> {
        return FlatnoteBlockParser().parseBlocks(FlatnoteLineParser().parseLines(content.lines()))
    }

    @Test
    fun `should read empty blocks`() {
        expect(Block.Empty(emptyLineCount = 2)) {
            parseSingleBlock("""
                
               
                some text required for kotlin to trim the indent correctly
            """.trimIndent())
        }
    }

    @Test
    fun `should read text block`() {
        expect(Block.Text(listOf(
                Line.Text("line 1", indent = 0),
                Line.Text("line 2", indent = 2),
        ))) {
            parseSingleBlock("""
                line 1
                  line 2
            """.trimIndent())
        }
    }

    @Test
    fun `should read list block`() {
        expect(Block.List(listOf(
                Line.ListItem("item 1", bullet = "-", indent = 0),
                Line.ListItem("item 2", bullet = "*", indent = 4),
        ))) {
            parseSingleBlock("""
                - item 1
                    * item 2
            """.trimIndent())
        }
    }

    @Test
    fun `should read header block`() {
        expect(Block.Header(Line.Header("header", 1))) {
            parseSingleBlock("# header")
        }
    }

    @Test
    fun `should read multiple header blocks`() {
        expect(listOf(
                Block.Header(Line.Header("header 1", level = 1)),
                Block.Header(Line.Header("header 2", level = 2))
        )) {
            parseBlocks("""
                # header 1
                ## header 2
            """.trimIndent())
        }
    }

    @Test
    fun `should read quote blocks`() {
        expect(Block.Quote(listOf(
                Line.Quote("quote 1"),
                Line.Quote("quote 2"),
        ))) {
            parseSingleBlock("""
                > quote 1
                > quote 2
            """.trimIndent())
        }
    }

    @Test
    fun `should read code blocks`() {
        expect(Block.Code(listOf(
                Line.Text("code 1", indent = 0),
                Line.Text("code 2", indent = 0)
        ), language = null)) {
            parseSingleBlock("""
                ```
                code 1
                code 2
                ```
            """.trimIndent())
        }


        expect(Block.Code(listOf(
                Line.Text("python code", indent = 0),
        ), language = "python")) {
            parseSingleBlock("""
                ```python
                python code
                ```
            """.trimIndent())
        }
    }

    @Test
    fun `should read frontmatter blocks`() {
        val block = """
            ---
            prop1: value
            prop2: other value
            ---
        """.trimIndent()

        expect(Block.Frontmatter(mapOf(
                "prop1" to "value",
                "prop2" to "other value",
        ))) { parseSingleBlock(block) }
    }

    @Test
    fun `should not read frontmatter block in the middle of the document`() {
        val block = """
            # header block
            ---
            key: value
            ---
        """.trimIndent()

        expect(listOf(
                Block.Header(Line.Header("header block", level = 1)),
                Block.Text(listOf(
                        Line.Text("---", indent = 0),
                        Line.Text("key: value", indent = 0),
                        Line.Text("---", indent = 0),
        )))) {
            parseBlocks(block)
        }
    }

    @Test
    fun `should read multiple blocks`() {
        val content = """
            ---
            key: value
            ---
            # header 1
            ## header 2
            simple text
            
            - list 1
            - list 2
            
            ```
            code 1
            code 2
            ```
            
            ```python
            python code
            ```
            
            > quote 1
            > quote 2
        """.trimIndent()

        expect(listOf(
                Block.Frontmatter(mapOf("key" to "value")),
                Block.Header(Line.Header("header 1", level = 1)),
                Block.Header(Line.Header("header 2", level = 2)),
                Block.Text(listOf(Line.Text("simple text", indent = 0))),
                Block.Empty(1),
                Block.List(listOf(
                        Line.ListItem("list 1", bullet = "-", indent = 0),
                        Line.ListItem("list 2", bullet = "-", indent = 0),
                )),
                Block.Empty(1),
                Block.Code(listOf(
                        Line.Text("code 1", indent = 0),
                        Line.Text("code 2", indent = 0),
                ), language = null),
                Block.Empty(1),
                Block.Code(listOf(
                        Line.Text("python code", indent = 0),
                ), language = "python"),
                Block.Empty(1),
                Block.Quote(listOf(
                        Line.Quote("quote 1"),
                        Line.Quote("quote 2"),
                ))
        )) {
            parseBlocks(content)
        }
    }
}