package com.wutsi.application.chat.endpoint.message.dto

data class ChatUserDto(
    val id: String = "",
    val firstName: String? = null,
    val lastName: String? = null,
    val imageUrl: String? = null
)
