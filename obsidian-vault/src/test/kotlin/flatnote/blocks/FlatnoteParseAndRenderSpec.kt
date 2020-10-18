package flatnote.blocks

import gh.marad.sidecar.obsidianvault.flatnote.internal.FlatnoteBlockParser
import gh.marad.sidecar.obsidianvault.flatnote.internal.FlatnoteLineParser
import gh.marad.sidecar.obsidianvault.flatnote.internal.FlatnoteRenderer
import kotlin.test.Test
import kotlin.test.expect

class FlatnoteParseAndRenderSpec {

    @Test
    fun `should render parsed markdown the same way`() {
        val markdown = """
            # header
            
            text
            text 2
            
            - list
                * list 2
            > quote
            > quote 2
            
            
            ```
            code
            ```
            
            another text
        """.trimIndent()

        val blocks = FlatnoteBlockParser().parseBlocks(FlatnoteLineParser().parseLines(markdown.lines()))

        expect(markdown) { FlatnoteRenderer().render(blocks) }
    }
}