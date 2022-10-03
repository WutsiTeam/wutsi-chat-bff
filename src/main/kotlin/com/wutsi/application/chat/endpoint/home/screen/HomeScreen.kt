package com.wutsi.application.chat.endpoint.home.screen

import com.wutsi.application.chat.endpoint.AbstractQuery
import com.wutsi.application.chat.endpoint.Page
import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.ui.Avatar
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.chat.WutsiChatApi
import com.wutsi.platform.chat.dto.Conversation
import com.wutsi.platform.chat.dto.SearchConversationRequest
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.SearchContactRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@RestController
@RequestMapping("/")
class HomeScreen(
    private val chatApi: WutsiChatApi,
    private val accountApi: WutsiAccountApi,
    private val contactApi: WutsiContactApi
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val recipientIds = mutableSetOf<Long>()

        // Conversations
        val accountId = securityContext.currentAccountId()
        val conversations = chatApi.searchConversations(
            request = SearchConversationRequest(
                accountId = accountId,
                limit = 50
            )
        ).conversations
        recipientIds.addAll(
            conversations.flatMap { listOf(it.lastMessage.senderId, it.lastMessage.recipientId) }
        )

        // Contacts
        val contactIds = contactApi.searchContact(
            request = SearchContactRequest(
                limit = 100,
                offset = 0
            )
        ).contacts.filter { !recipientIds.contains(it.contactId) }.map { it.contactId }
        recipientIds.addAll(contactIds)

        // Recipients
        val recipients = accountApi.searchAccount(
            request = SearchAccountRequest(
                ids = recipientIds.toList(),
                business = true, // CHAT feature only for business account
                limit = recipientIds.size
            )
        ).accounts.associateBy { it.id }

        // Conversations items
        val children = mutableListOf<WidgetAware>()
        children.addAll(toConversationListItems(conversations, recipients))
        children.addAll(toContactListItems(contactIds, recipients))

        return Screen(
            id = Page.CHAT,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_PRIMARY,
                foregroundColor = Theme.COLOR_WHITE,
                title = getText("page.chat.app-bar.title")
            ),
            child = Container(
                child = Flexible(
                    child = ListView(
                        separatorColor = Theme.COLOR_DIVIDER,
                        separator = true,
                        children = children
                    )
                )
            ),
            bottomNavigationBar = bottomNavigationBar()
        ).toWidget()
    }

    private fun toConversationListItems(
        conversations: List<Conversation>,
        recipients: Map<Long, AccountSummary>
    ): List<WidgetAware> {
        val accountId = securityContext.currentAccountId()
        val account = securityContext.currentAccount()
        return conversations.mapNotNull {
            val message = it.lastMessage
            val recipient =
                if (message.senderId == accountId) recipients[message.recipientId] else recipients[message.senderId]

            if (recipient != null && recipient.status == "ACTIVE") {
                ListItem(
                    leading = Avatar(model = sharedUIMapper.toAccountModel(recipient), radius = 24.0),
                    trailing = Text(
                        caption = formatDateTime(message.timestamp, account),
                        size = Theme.TEXT_SIZE_SMALL,
                        color = Theme.COLOR_PRIMARY
                    ),
                    caption = recipient.displayName ?: "",
                    subCaption = message.text.take(50),
                    action = gotoUrl(
                        urlBuilder.build("messages?recipient-id=${recipient.id}")
                    )
                )
            } else {
                null
            }
        }
    }

    private fun toContactListItems(contactIds: List<Long>, recipients: Map<Long, AccountSummary>): List<WidgetAware> {
        return contactIds.mapNotNull {
            val recipient = recipients[it]
            if (recipient != null && recipient.status == "ACTIVE") {
                ListItem(
                    leading = Avatar(model = sharedUIMapper.toAccountModel(recipient), radius = 24.0),
                    caption = recipient.displayName ?: "",
                    action = gotoUrl(
                        urlBuilder.build("messages?recipient-id=${recipient.id}")
                    )
                )
            } else {
                null
            }
        }.sortedBy { it.caption }
    }

    private fun formatDateTime(timestamp: Long, account: Account): String {
        val tz = account.timezoneId?.let { TimeZone.getTimeZone(it) } ?: TimeZone.getDefault()
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            tz.toZoneId()
        )
        return if (dateTime.toLocalDate() == LocalDate.now()) {
            dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        }
    }
}
