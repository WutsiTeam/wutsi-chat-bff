package com.wutsi.application.chat.endpoint.home.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.chat.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.account.entity.AccountStatus
import com.wutsi.platform.chat.dto.Conversation
import com.wutsi.platform.chat.dto.Message
import com.wutsi.platform.chat.dto.SearchConversationResponse
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.ContactSummary
import com.wutsi.platform.contact.dto.SearchContactResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

internal class HomeScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    private lateinit var contactApi: WutsiContactApi

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
            createConversation("99", 99, ACCOUNT_ID, "Hello World")
        )
        doReturn(SearchConversationResponse(conversations)).whenever(chatApi).searchConversations(any())

        val contact = listOf(
            createContact(1),
            createContact(10),
            createContact(20),
            createContact(999)
        )
        doReturn(SearchContactResponse(contact)).whenever(contactApi).searchContact(any())

        val accounts = listOf(
            createAccountSummary(ACCOUNT_ID),
            createAccountSummary(1),
            createAccountSummary(2),
            createAccountSummary(3),
            createAccountSummary(10),
            createAccountSummary(20),
            createAccountSummary(99, AccountStatus.SUSPENDED),
            createAccountSummary(999, AccountStatus.SUSPENDED)
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

    private fun createContact(contactId: Long) = ContactSummary(
        accountId = ACCOUNT_ID,
        contactId = contactId
    )
}
