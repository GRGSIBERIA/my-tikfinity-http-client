package com.mytk

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.Identifier
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

object MyTikFinityHttpClient : ModInitializer {
	const val MOD_ID: String = "my-tikfinity-http-client"
	private val LOGGER = LoggerFactory.getLogger(MOD_ID)
	
	private const val HTTP_PORT = 8832
	private const val ACTION_EXEC_PATH = "/api/features/actions/exec"
	
	private val httpClient: TikFinityClient

	override fun onInitialize() {
		
	}

	fun id(path: String): Identifier
		= Identifier.fromNamespaceAndPath(MOD_ID, path)
}
