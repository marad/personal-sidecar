package gh.marad.sidecar.obsidianvault.flatnote

interface FlatnoteFacade {
    fun parse(markdown: String): List<Block>
    fun parse(markdown: List<String>): List<Block>
    fun render(blocks: List<Block>): String
}