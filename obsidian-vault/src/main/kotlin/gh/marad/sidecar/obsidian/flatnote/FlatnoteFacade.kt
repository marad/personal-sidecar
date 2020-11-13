package gh.marad.sidecar.obsidian.flatnote

interface FlatnoteFacade {
    fun parse(markdown: String): List<Block>
    fun parse(markdown: List<String>): List<Block>
    fun render(blocks: List<Block>): String
}