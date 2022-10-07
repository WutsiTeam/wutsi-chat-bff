package com.wutsi.application.chat.endpoint.message.dto

data class ChatMessageDto(
    val id: String = "",
    val type: String = "text",
    val createdAt: Long = -1,
    val roomId: String = "",
    val text: String = "",
    val author: ChatUserDto = ChatUserDto(),
    val status: String = ""
)
