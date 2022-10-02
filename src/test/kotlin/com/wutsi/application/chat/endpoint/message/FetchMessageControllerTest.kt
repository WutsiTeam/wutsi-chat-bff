package com.wutsi.application.chat.endpoint.message

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.chat.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.chat.dto.Message
import com.wutsi.platform.chat.dto.SearchMessageResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class FetchMessageControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/messages/fetch?recipient-id=1"
    }

    @Test
    fun index() {
        val messages = listOf(
            createMessage("A", ACCOUNT_ID, 1, "Hello", 1000),
            createMessage("B", 1, ACCOUNT_ID, "How are you", 900),
            createMessage("C", ACCOUNT_ID, 1, "I'm fine", 800),
        )
        doReturn(SearchMessageResponse(messages)).whenever(chatApi).searchMessages(any())

        val accounts = listOf(
            createAccountSummary(ACCOUNT_ID),
            createAccountSummary(1)
        )
        doReturn(SearchAccountResponse(accounts)).whenever(accountApi).searchAccount(any())

        assertEndpointEquals("/screens/messages/fetch.json", url)
    }

    private fun createMessage(referenceId: String, senderId: Long, recipientId: Long, text: String, timestamp: Long) =
        Message(
            referenceId = referenceId,
            senderId = senderId,
            recipientId = recipientId,
            text = text,
            timestamp = timestamp
        )
}
