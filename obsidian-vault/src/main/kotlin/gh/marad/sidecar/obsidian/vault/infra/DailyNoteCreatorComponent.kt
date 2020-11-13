package gh.marad.sidecar.obsidian.vault.infra

import gh.marad.sidecar.obsidian.vault.ObsidianVault
import gh.marad.sidecar.obsidian.vault.internal.DailyNoteCreator
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Deactivate
import org.osgi.service.component.annotations.Reference

@Component
class DailyNoteCreatorComponent {
    @Reference
    private var obsidianVault: ObsidianVault? = null
    private var dailyNoteCreator: DailyNoteCreator? = null


    @Activate
    fun setup() {
        val vault = obsidianVault ?: throw RuntimeException("There is no obsidian vault service!")
        dailyNoteCreator = DailyNoteCreator(vault)
    }

    @Deactivate
    fun teardown() {
        dailyNoteCreator?.stop()
        dailyNoteCreator = null
    }
}

