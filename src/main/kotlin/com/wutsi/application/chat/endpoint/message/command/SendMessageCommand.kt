package com.wutsi.application.chat.endpoint.message.command

import com.wutsi.application.chat.endpoint.AbstractCommand
import com.wutsi.application.chat.endpoint.message.dto.ChatMessageDto
import com.wutsi.platform.chat.WutsiChatApi
import com.wutsi.platform.chat.dto.SendMessageRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/send")
class SendMessageCommand(
    private val chatApi: WutsiChatApi
) : AbstractCommand() {
    @PostMapping
    fun index(
        @RequestBody msg: ChatMessageDto
    ) {
        chatApi.sendMessage(
            request = SendMessageRequest(
                referenceId = msg.id,
                recipientId = msg.roomId.toLong(),
                text = msg.text,
                timestamp = msg.createdAt
            )
        )
    }
}
