package gh.marad.sidecar.obsidianwebclipper

import khttp.get
import khttp.post
import org.json.JSONObject

interface WebClipper {
    fun fetchLinks(syncMarker: String): List<LinkClip>
    fun fetchNotes(syncMarker: String): List<NoteClip>

    data class LinkClip(val title: String?, val url: String, val content: String?)
    data class NoteClip(val title: String?, val content: String?)
}

class WebClipperConfig {
    fun createPushbulletWebClipper(pushbulletClient: PushbulletClient): WebClipper {
        return PushbulletWebClipper(pushbulletClient)
    }
}

private class PushbulletWebClipper(
        private val client: PushbulletClient
) : WebClipper {

    private val clipperDeviceId = acquireClipperDeviceId("markdown-clipper")

    private fun acquireClipperDeviceId(clipperDeviceName: String): String {
        val device = client.listDevices().firstOrNull { it.nickname == clipperDeviceName }
        if (device == null) {
            client.createDevice(clipperDeviceName)
        }
        return client.listDevices().firstOrNull { it.nickname == clipperDeviceName }?.id ?: throw RuntimeException("Couldn't find nor create markdown clipper device")
    }

    override fun fetchLinks(syncMarker: String): List<WebClipper.LinkClip> {
        return client.listPushes(syncMarker.toLong()).pushes
                .filterIsInstance<PushbulletClient.Push.Link>()
                .filter { it.targetDeviceId == clipperDeviceId }
                .map {
                    WebClipper.LinkClip(
                            title = it.title,
                            url = it.url,
                            content = it.body
                    )
                }
    }

    override fun fetchNotes(syncMarker: String): List<WebClipper.NoteClip> {
        return client.listPushes(syncMarker.toLong()).pushes
                .filterIsInstance<PushbulletClient.Push.Note>()
                .filter { it.targetDeviceId == clipperDeviceId }
                .map {
                    WebClipper.NoteClip(
                            title = it.title,
                            content = it.body
                    )
                }
    }
}


//fun main() {
//    val baseUrl = "https://api.pushbullet.com"
//    val accessToken = "o.2dcdtsXrXZZMBEaM5Cd2ENGAP28PlnE7"
//    val client = PushbulletClient(baseUrl, accessToken)
//    val queue = WebClipperConfig().createPushbulletWebClipper(client)
//
//    val timestamp = "1577836800"
//    val links = queue.fetchLinks(timestamp)
//}
//
class PushbulletClient(private val baseUrl: String, token: String) {
    private val headers = mapOf("Access-Token" to token, "Content-Type" to "application/json")


    fun createDevice(nickname: String) {
        post("$baseUrl/v2/devices", headers = headers, data = """{"nickname":"$nickname"}""")
    }

    fun listDevices(): List<Device> {
        val response = get("$baseUrl/v2/devices", headers = headers)
        return response.jsonObject
                .getJSONArray("devices")
                .map { it as JSONObject }
                .map { Device(
                        id = it.getString("iden"),
                        nickname = if (it.has("nickname")) it.getString("nickname") else null
                ) }
    }

    fun listPushes(sinceTimestamp: Long): Pushes {
        val json = get("$baseUrl/v2/pushes?modified_after=$sinceTimestamp&receiver_iden=ujDu3iYen6asjAZG5vWocC", headers = headers).jsonObject
        return Pushes(
                cursor = if (json.has("cursor")) json.getString("cursor") else null,
                pushes = json.getJSONArray("pushes")
                        .map { it as JSONObject }
                        .flatMap {
                            when {
                                it.getStringOrNull("type") == "link" -> listOf(Push.Link(
                                        it.getStringOrNull("target_device_iden"),
                                        it.getBoolean("dismissed"),
                                        it.getStringOrNull("title"),
                                        it.getString("url"),
                                        it.getStringOrNull("body")
                                ))
                                it.getStringOrNull("type") == "note" -> listOf(Push.Note(
                                        it.getStringOrNull("target_device_iden"),
                                        it.getBoolean("dismissed"),
                                        it.getStringOrNull("title"),
                                        it.getStringOrNull("body")
                                ))
                                else -> emptyList()
                            }
                        }
        )
    }

    data class Device(val id: String, val nickname: String?)
    sealed class Push {
        data class Link(val targetDeviceId: String?, val dismissed: Boolean, val title: String?, val url: String, val body: String?) : Push()
        data class Note(val targetDeviceId: String?, val dismissed: Boolean, val title: String?, val body: String?) : Push()
    }
    data class Pushes(val pushes: List<Push>,
                      val cursor: String?)

    private fun JSONObject.getStringOrNull(key: String): String? = if (this.has(key)) this.getString(key) else null
}

