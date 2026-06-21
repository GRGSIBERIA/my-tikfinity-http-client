package com.mytk.client

import org.slf4j.LoggerFactory
import com.mytk.client.VoiceAPI

data class TriggerEvent(
	val context: TikFinityContext,
) {
	companion object {
		private const val MOD_ID: String = "my-tikfinity-http-client"
		private val LOGGER = LoggerFactory.getLogger(MOD_ID)
		
		private const val NAHIDA_STYLE_NAME = "nahida-style"
		private const val FURINA_STYLE_NAME = "furina"
	}

	val type: Type = Type.fromId(context.triggerTypeId)
	private val nahidaModelId: Int = -1
	private val furinaModelId: Int = -1
	
	operator fun invoke() {
		when (type) {
			Type.SHARE -> onShare()
			Type.COMMAND -> onCommand()
			Type.GIFT_MINIMUM_COINS -> onGiftMinimumCoins()
			Type.GIFT_SPECIFIC -> onGiftSpecific()
			Type.JOIN -> onJoin()
			Type.LIKES -> onLikes()
			Type.FOLLOW -> onFollow()
			Type.SUBSCRIBE -> onSubscribe()
			Type.CHAT -> onChat()
			Type.EMOTE -> onEmote()
			Type.FIRST_USER_ACTIVITY -> onFirstUserActivity()
		}
	}

	private fun onShare() {
		LOGGER.info("Trigger event: {}", Type.SHARE.name)
	}

	private fun onCommand() {
		LOGGER.info("Trigger event: {}", Type.COMMAND.name)
	}

	private fun onGiftMinimumCoins() {
		LOGGER.info("Trigger event: {}", Type.GIFT_MINIMUM_COINS.name)
	}

	private fun onGiftSpecific() {
		LOGGER.info("Trigger event: {}", Type.GIFT_SPECIFIC.name)
	}

	private fun onJoin() {
		LOGGER.info("Trigger event: {}", Type.JOIN.name)
	}

	private fun onLikes() {
		LOGGER.info("Trigger event: {}", Type.LIKES.name)
		LOGGER.info("{}: {}", context.nickname, context.repeatCount)
	}

	private fun onFollow() {
		LOGGER.info("Trigger event: {}", Type.FOLLOW.name)
		LOGGER.info("Followed: {}", context.nickname)
	}

	private fun onSubscribe() {
		LOGGER.info("Trigger event: {}", Type.SUBSCRIBE.name)
		LOGGER.info("Subscribed: {}", context.nickname)
	}

	private fun onChat() {
		LOGGER.info("Trigger event: {}", Type.CHAT.name)
		LOGGER.info("{}: {}", context.nickname, context.commandParams)
		
		val voice = VoiceAPI()
		val js = voice.getModelsJson()
		val modelId = voice.getModelId(js, "nahida")
	}

	private fun onEmote() {
		LOGGER.info("Trigger event: {}", Type.EMOTE.name)
	}

	private fun onFirstUserActivity() {
		LOGGER.info("Trigger event: {}", Type.FIRST_USER_ACTIVITY.name)
	}

	enum class Type(val id: Int) {
		SHARE(1),
		COMMAND(2),
		GIFT_MINIMUM_COINS(3),
		GIFT_SPECIFIC(4),
		JOIN(6),
		LIKES(7),
		FOLLOW(9),
		SUBSCRIBE(10),
		CHAT(11),
		EMOTE(12),
		FIRST_USER_ACTIVITY(13),
		;

		companion object {
			fun fromId(id: Int): Type = entries.firstOrNull { it.id == id }
				?: throw IllegalArgumentException("Unknown triggerTypeId: $id")
		}
	}
}
