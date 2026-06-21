package com.mytk.client

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class VoiceAPI() {
	companion object {
		private const val MODELS_INFO_URL = "http://localhost:5000/models/info"
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
	}
}
