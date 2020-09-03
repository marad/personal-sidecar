package gh.marad.sidecar.obsidianvault

import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Deactivate
import org.osgi.service.component.annotations.Reference
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Component
class DailyNoteCreator {
    private var timer: Timer? = null

    @Reference
    private var obsidianVault: ObsidianVault? = null

    @Activate
    fun setup() {
        timer = Timer()
        timer?.scheduleAtFixedRate(CreateDailyNoteTask(
                timer!!, obsidianVault ?: throw RuntimeException("There is no obsidian vault service!")
        ), 0, 1000)
    }

    @Deactivate
    fun teardown() {
        timer?.cancel()
        timer = null
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