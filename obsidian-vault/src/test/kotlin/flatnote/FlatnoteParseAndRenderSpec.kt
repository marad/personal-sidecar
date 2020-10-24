package flatnote

import gh.marad.sidecar.obsidianvault.flatnote.FlatnoteConfig
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
            
            ```python
            python code
            ```
            
            another text
        """.trimIndent()

        val flatnote = FlatnoteConfig().createFlatnote()
        val blocks = flatnote.parse(markdown)
        expect(markdown) { flatnote.render(blocks) }
    }
}