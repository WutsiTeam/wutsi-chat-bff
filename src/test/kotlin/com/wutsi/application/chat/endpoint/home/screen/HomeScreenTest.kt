package com.wutsi.application.chat.endpoint.home.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.chat.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.chat.dto.Conversation
import com.wutsi.platform.chat.dto.Message
import com.wutsi.platform.chat.dto.SearchConversationResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class HomeScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port"
    }

    @Test
    fun index() {
        // GIVEN
        val conversations = listOf(
            createConversation("1", ACCOUNT_ID, 1, "Yo"),
            createConversation("2", 2, ACCOUNT_ID, "Yo"),
            createConversation("3", 3, ACCOUNT_ID, "Hello World"),
        )
        doReturn(SearchConversationResponse(conversations)).whenever(chatApi).searchConversations(any())

        val accounts = listOf(
            createAccountSummary(ACCOUNT_ID),
            createAccountSummary(1),
            createAccountSummary(2),
            createAccountSummary(3)
        )
        doReturn(SearchAccountResponse(accounts)).whenever(accountApi).searchAccount(any())

        // WHEN
        assertEndpointEquals("/screens/home/index.json", url)
    }

    private fun createConversation(id: String, senderId: Long, recipientId: Long, text: String) = Conversation(
        id = id,
        lastMessage = Message(
            senderId = senderId,
            recipientId = recipientId,
            text = text
        )
    )
}
