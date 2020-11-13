package gh.marad.sidecar.obsidian.flatnote

import gh.marad.sidecar.obsidian.flatnote.internal.FlatnoteImpl

class FlatnoteConfig {
    fun createFlatnote(): FlatnoteFacade {
        return FlatnoteImpl()
    }
}