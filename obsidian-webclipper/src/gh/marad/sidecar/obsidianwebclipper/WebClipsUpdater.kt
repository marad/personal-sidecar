package gh.marad.sidecar.obsidianwebclipper

import gh.marad.sidecar.obsidianvault.ObsidianVault
import org.osgi.service.cm.ConfigurationAdmin
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Deactivate
import org.osgi.service.component.annotations.Reference
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Instant
import java.util.*
import kotlin.concurrent.timerTask

@Component
class WebClipsUpdater {
    @Reference
    private var obsidianVault: ObsidianVault? = null
    @Reference
    private lateinit var configAdmin: ConfigurationAdmin
    private val log = LoggerFactory.getLogger(WebClipsUpdater::class.java)

    private lateinit var clipper: WebClipper

    private val timer = Timer()

    @Activate
    fun init() {
        val configuration = configAdmin.getConfiguration(Constants.CONFIG_PID)
        val accessToken = (configuration.properties[CONFIG_ACCESS_TOKEN]
                ?: throw RuntimeException("Access token is required for web clipper to work!")) as String
        val client = PushbulletClient("https://api.pushbullet.com", accessToken)
        clipper = WebClipperConfig().createPushbulletWebClipper(client)
        timer.scheduleAtFixedRate(updateTask, START_NOW, TEN_MINUTES)
    }

    private val updateTask = timerTask {
        log.info("Fetching web clipper contents...")
        val configuration = configAdmin.getConfiguration(Constants.CONFIG_PID)
        val newSyncMarker = Instant.now(Clock.systemUTC()).epochSecond
        val syncMarker = (configuration.properties[CONFIG_SYNC_MARKER]
                ?: newSyncMarker) as Long

        val links = clipper.fetchLinks(syncMarker.toString())
        val notes = clipper.fetchNotes(syncMarker.toString())

        log.info("Fetched ${links.size} links and ${notes.size} notes.")
        links.forEach { obsidianVault?.appendUrlToInbox(it.url, it.content) }
        notes .forEach {
            if (it.content != null) {
                obsidianVault?.appendNoteToInbox(it.content)
            } else if (it.title != null) {
                obsidianVault?.appendNoteToInbox(it.title)
            }
        }

        val properties = configuration.properties
        properties.put(CONFIG_SYNC_MARKER, newSyncMarker)
        configuration.update(properties)
    }

    @Deactivate
    fun cleanup() {
        timer.cancel()
    }

    companion object {
        const val START_NOW = 0L
        const val TEN_MINUTES = 10 * 60 * 1000L
        const val CONFIG_ACCESS_TOKEN = "pushbullet.accessToken"
        const val CONFIG_SYNC_MARKER = "pushbullet.syncMarker"
    }
}