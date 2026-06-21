package com.mytk.client

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.ByteArrayInputStream
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl
import javax.sound.sampled.LineEvent
import kotlin.math.log10
import org.slf4j.LoggerFactory

class VoiceAPI {
	companion object {
		private const val MOD_ID: String = "my-tikfinity-http-client"
		private val LOGGER = LoggerFactory.getLogger(MOD_ID)
		private const val MODELS_INFO_URL = "http://localhost:5000/models/info"
		private const val VOICE_URL = "http://localhost:5000/voice"
	}

	private val httpClient = HttpClient.newHttpClient()

	fun getModelsJson(): JsonObject {
		val request = HttpRequest.newBuilder()
			.uri(URI.create(MODELS_INFO_URL))
			.GET()
			.build()
		val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

		return JsonParser.parseString(response.body()).asJsonObject
	}

	fun getModelId(models: JsonObject, styleName: String): Int {
		for ((modelId, model) in models.entrySet()) {
			val speakerName = model.asJsonObject
				.getAsJsonObject("id2spk")
				.get("0")
				.asString

			if (speakerName == styleName) {
				return modelId.toInt()
			}
		}

		throw NoSuchElementException("Style name is not found: $styleName")
	}

	fun playText(
		text: String,
		styleName: String,
		modelId: Int,
		weight: Double = 12.5,
		volume: Double = 1.0,
	) {
		require(volume >= 0.0) { "Volume must be greater than or equal to 0" }

		val query = listOf(
			"text" to text,
			"model_id" to modelId.toString(),
			"speaker_name" to styleName,
			"speaker_id" to "0",
			"split_interval" to "0.5",
			"style_weight" to weight.toString(),
		).joinToString("&") { (name, value) ->
			"${encode(name)}=${encode(value)}"
		}
		val request = HttpRequest.newBuilder()
			.uri(URI.create("$VOICE_URL?$query"))
			.POST(HttpRequest.BodyPublishers.noBody())
			.build()
		val response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray())

		check(response.statusCode() in 200..299) {
			"Voice API request failed: HTTP ${response.statusCode()}"
		}

		ByteArrayInputStream(response.body()).use { wavInputStream ->
			AudioSystem.getAudioInputStream(wavInputStream).use { audioInputStream ->
				playAudio(audioInputStream, volume)
			}
		}
	}

	private fun encode(value: String): String =
		URLEncoder.encode(value, StandardCharsets.UTF_8)

	private fun playAudio(audioInputStream: javax.sound.sampled.AudioInputStream, volume: Double) {
		val playbackFinished = CountDownLatch(1)
		val clip = AudioSystem.getClip()

		try {
			clip.addLineListener { event ->
				if (event.type == LineEvent.Type.STOP) {
					playbackFinished.countDown()
				}
			}
			clip.open(audioInputStream)
			setVolume(clip, volume)
			clip.start()
			playbackFinished.await()
		} catch (e: Exception) {
			LOGGER.error("Failed to play audio", e)
		} finally {
			clip.close()
		}
	}

	private fun setVolume(clip: Clip, volume: Double) {
		if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			return
		}

		val gainControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
		val gain = if (volume == 0.0) {
			gainControl.minimum
		} else {
			(20.0 * log10(volume)).toFloat()
				.coerceIn(gainControl.minimum, gainControl.maximum)
		}
		gainControl.value = gain
	}
}
