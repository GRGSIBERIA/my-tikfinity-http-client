package com.mytk.client

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import net.fabricmc.api.ClientModInitializer
import net.minecraft.resources.Identifier
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import com.mytk.client.TikFinityClient

object MyTikFinityHttpClientClient : ClientModInitializer {
	const val MOD_ID: String = "my-tikfinity-http-client"
	private const val HTTP_PORT = 8832
	private const val ACTION_EXEC_PATH = "/api/features/actions/exec"

	private val LOGGER = LoggerFactory.getLogger(MOD_ID)
	
	override fun onInitializeClient() {
		
	}
	
	fun id(path: String): Identifier
		= Identifier.fromNamespaceAndPath(MOD_ID, path)
}
