package com.mytk.client

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

class TikFinityClient {
    
    companion object {
        const val MOD_ID: String = "my-tikfinity-http-client"
        private const val HTTP_PORT = 8832
        private const val ACTION_EXEC_PATH = "/api/features/actions/exec"

        private val LOGGER = LoggerFactory.getLogger(MOD_ID)
        private val GSON = Gson()
    }

	private val triggerEvent: TriggerEvent by lazy { TriggerEvent() }

	
	fun startHttpServer() {
		val server = HttpServer.create(InetSocketAddress(HTTP_PORT), 0)
		server.createContext(ACTION_EXEC_PATH, ::handleActionExec)

		Thread(server::start, "$MOD_ID-http-server-starter").apply {
			isDaemon = true
			start()
			join()
		}

		LOGGER.info("HTTP server started on port {}", HTTP_PORT)
	}
	

	private fun handleActionExec(exchange: HttpExchange) {
		exchange.responseHeaders.apply {
			add("Access-Control-Allow-Origin", "*")
			add("Access-Control-Allow-Headers", "*")
			add("Access-Control-Allow-Methods", "*")
		}

		if (exchange.requestURI.path != ACTION_EXEC_PATH) {
			exchange.sendResponseHeaders(404, -1)
			exchange.close()
			return
		}

		if (exchange.requestMethod == "OPTIONS") {
			exchange.sendResponseHeaders(204, -1)
			exchange.close()
			return
		}

		if (exchange.requestMethod != "POST") {
			exchange.sendResponseHeaders(405, -1)
			exchange.close()
			return
		}

		val tikFinityContext = runCatching {
			val requestBody = exchange.requestBody
				.bufferedReader(StandardCharsets.UTF_8)
				.use { it.readText() }
			val contextJson = JsonParser.parseString(requestBody)
				.asJsonObject
				.getAsJsonObject("context")
				?: error("Request body does not contain context")

			GSON.fromJson(contextJson, TikFinityContext::class.java)
		}.getOrElse { exception ->
			LOGGER.warn("Invalid TikFinity request body", exception)
			exchange.sendResponseHeaders(400, -1)
			exchange.close()
			return
		}

		triggerEvent(tikFinityContext)
		exchange.sendResponseHeaders(200, -1)
		exchange.close()
	}
}
