package gh.marad.sidecar.obsidian.flatnote.internal

import gh.marad.sidecar.obsidian.flatnote.Block
import gh.marad.sidecar.obsidian.flatnote.FlatnoteFacade

class FlatnoteImpl : FlatnoteFacade {
    override fun parse(markdown: String): List<Block> {
        return parse(markdown.lines())
    }

    override fun parse(markdown: List<String>): List<Block> {
        return FlatnoteBlockParser().parseBlocks(FlatnoteLineParser().parseLines(markdown))
    }

    override fun render(blocks: List<Block>): String {
        return FlatnoteRenderer().render(blocks)
    }
}