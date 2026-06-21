package com.mytk

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import net.fabricmc.api.ClientModInitializer
import net.minecraft.resources.Identifier
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

class TikFinityClient {
    
    companion object {
        const val MOD_ID: String = "my-tikfinity-http-client"
        private const val HTTP_PORT = 8832
        private const val ACTION_EXEC_PATH = "/api/features/actions/exec"

        private val LOGGER = LoggerFactory.getLogger(MOD_ID)
    }

	
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

		exchange.sendResponseHeaders(200, -1)
		exchange.close()
	}
}