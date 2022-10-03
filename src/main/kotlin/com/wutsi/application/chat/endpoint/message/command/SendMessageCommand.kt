package com.wutsi.application.chat.endpoint.message.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.chat.endpoint.AbstractCommand
import com.wutsi.application.chat.endpoint.message.dto.ChatMessageDto
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.chat.event.EventURN
import com.wutsi.platform.core.stream.EventStream
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/send")
class SendMessageCommand(
    private val objectMapper: ObjectMapper,
    private val eventStream: EventStream
) : AbstractCommand() {
    @PostMapping
    fun index(
        @RequestBody msg: ChatMessageDto
    ) {
        val request = SendMessageRequest(
            referenceId = msg.id,
            recipientId = msg.roomId.toLong(),
            text = msg.text,
            timestamp = msg.createdAt
        )
        eventStream.publish(
            type = EventURN.MESSAGE_SUBMITTED.urn,
            payload = objectMapper.writeValueAsString(request)
        )
    }
}
