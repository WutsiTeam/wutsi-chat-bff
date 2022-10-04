package com.wutsi.application.chat.endpoint.message.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.chat.endpoint.AbstractEndpointTest
import com.wutsi.application.chat.endpoint.message.dto.ChatMessageDto
import com.wutsi.application.chat.endpoint.message.dto.ChatUserDto
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.chat.dto.SendMessageResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SendMessageCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/send?recipient-id=100"
    }

    @Test
    fun send() {
        // GIVEN
        doReturn(SendMessageResponse()).whenever(chatApi).sendMessage(any())

        // WHEN
        val msg = ChatMessageDto(
            id = "1111",
            createdAt = 1111L,
            roomId = "100,$ACCOUNT_ID",
            text = "Hello",
            author = ChatUserDto(
                id = ACCOUNT_ID.toString(),
                firstName = "Ray",
                lastName = "Sponsible"
            )
        )
        rest.postForEntity(url, msg, Any::class.java)

        // THEN
        val request = argumentCaptor<SendMessageRequest>()
        verify(chatApi).sendMessage(request.capture())

        assertEquals(msg.id, request.firstValue.referenceId)
        assertEquals(msg.createdAt, request.firstValue.timestamp)
        assertEquals(100, request.firstValue.recipientId)
        assertEquals(msg.roomId, request.firstValue.conversationId)
        assertEquals(msg.text, request.firstValue.text)
    }
}
