package gh.marad.sidecar.obsidianvault.flatnote.internal

import gh.marad.sidecar.obsidianvault.flatnote.Block
import gh.marad.sidecar.obsidianvault.flatnote.FlatnoteFacade

class FlatnoteImpl : FlatnoteFacade {
    override fun parse(markdown: String): List<Block> {
        return FlatnoteBlockParser().parseBlocks(FlatnoteLineParser().parseLines(markdown.lines()))
    }

    override fun render(blocks: List<Block>): String {
        return FlatnoteRenderer().render(blocks)
    }
}