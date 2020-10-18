package gh.marad.sidecar.obsidianvault.flatnote

import gh.marad.sidecar.obsidianvault.flatnote.internal.FlatnoteImpl

class FlatnoteConfig {
    fun createFlatnote(): FlatnoteFacade {
        return FlatnoteImpl()
    }
}