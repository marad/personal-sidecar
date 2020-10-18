package gh.marad.sidecar.obsidianvault.flatnote

import gh.marad.sidecar.obsidianvault.flatnote.internal.Block

interface FlatnoteFacade {
    fun parse(markdown: String): List<Block>
    fun render(blocks: List<Block>)
}