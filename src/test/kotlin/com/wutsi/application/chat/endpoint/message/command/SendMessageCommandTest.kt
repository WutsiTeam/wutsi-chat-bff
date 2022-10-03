package com.wutsi.application.chat.endpoint.message.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.chat.endpoint.AbstractEndpointTest
import com.wutsi.application.chat.endpoint.message.dto.ChatMessageDto
import com.wutsi.application.chat.endpoint.message.dto.ChatUserDto
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.chat.dto.SendMessageResponse
import com.wutsi.platform.chat.event.EventURN
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

internal class SendMessageCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var eventStream: EventStream

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/send"
    }

    @Test
    fun send() {
        // GIVEN
        doReturn(SendMessageResponse()).whenever(chatApi).sendMessage(any())

        // WHEN
        val msg = ChatMessageDto(
            id = "1111",
            createdAt = 1111L,
            roomId = "111",
            text = "Hello",
            author = ChatUserDto(
                id = ACCOUNT_ID.toString(),
                firstName = "Ray",
                lastName = "Sponsible"
            )
        )
        rest.postForEntity(url, msg, Any::class.java)

        // THEN
        val payload = argumentCaptor<String>()
        verify(eventStream).publish(eq(EventURN.MESSAGE_SUBMITTED.urn), payload.capture())

        val request = objectMapper.readValue(payload.firstValue, SendMessageRequest::class.java)
        assertEquals(msg.id, request.referenceId)
        assertEquals(msg.createdAt, request.timestamp)
        assertEquals(msg.roomId.toLong(), request.recipientId)
        assertEquals(msg.text, request.text)
    }
}
