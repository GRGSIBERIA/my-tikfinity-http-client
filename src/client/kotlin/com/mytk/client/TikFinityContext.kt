package com.mytk

data class TikFinityContext(
	val userId: String,
	val username: String,
	val nickname: String,
	val profilePicturUrl: String,
	val commandParams: String? = null,
	val giftId: Int? = null,
	val giftName: String? = null,
	val coins: Int = 0,
	val repeatCount: Int? = null,
	val triggerTypeId: Int,
)
