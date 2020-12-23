package gh.marad.sidecar.obsidian.vault.internal

import gh.marad.sidecar.obsidian.vault.ObsidianVault
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

internal class DailyNoteCreator(private val obsidianVault: ObsidianVault) {
    private val timer: Timer = Timer().also {
//        it.scheduleAtFixedRate(CreateDailyNoteTask(it, obsidianVault), 0, 1000)
    }

    fun stop() {
        timer.cancel()
    }

    private class CreateDailyNoteTask(
            private val timer: Timer,
            private val obsidianVault: ObsidianVault): TimerTask() {

        private val log = org.slf4j.LoggerFactory.getLogger(CreateDailyNoteTask::class.java)

        override fun run() {
            if (obsidianVault.dailyNoteExists()) {
                this.cancel()
                val tomorrow = Date.from(
                        LocalDate.now()
                                .plusDays(1)
                                .atStartOfDay(ZoneId.systemDefault()).toInstant())

                log.info("Daily note already created. Scheduling for tomorrow at $tomorrow")
                timer.scheduleAtFixedRate(CreateDailyNoteTask(timer, obsidianVault), tomorrow, 1000)
            } else {
                obsidianVault.createDailyNoteFromTemplate()
                log.info("Daily note created.")
            }
        }
    }
}