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
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.chat.WutsiChatApi
import com.wutsi.platform.chat.dto.SearchConversationRequest
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
    private val accountApi: WutsiAccountApi
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        // Conversations
        val accountId = securityContext.currentAccountId()
        val conversations = chatApi.searchConversations(
            request = SearchConversationRequest(
                accountId = accountId,
                limit = 50
            )
        ).conversations

        // Recipients
        val recipientIds = conversations.flatMap { listOf(it.lastMessage.senderId, it.lastMessage.recipientId) }.toSet()
        val recipients = accountApi.searchAccount(
            request = SearchAccountRequest(
                ids = recipientIds.toList(),
                limit = recipientIds.size
            )
        ).accounts.associateBy { it.id }

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
                        children = conversations.mapNotNull {
                            val message = it.lastMessage
                            val recipient =
                                if (message.senderId == accountId) recipients[message.recipientId] else recipients[message.senderId]

                            if (recipient != null) {
                                ListItem(
                                    leading = Avatar(model = sharedUIMapper.toAccountModel(recipient), radius = 24.0),
                                    trailing = Text(
                                        caption = formatDateTime(message.timestamp),
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
                    )
                )
            ),
            bottomNavigationBar = bottomNavigationBar()
        ).toWidget()
    }

    private fun formatDateTime(timestamp: Long): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            TimeZone.getDefault().toZoneId()
        )
        return if (dateTime.toLocalDate() == LocalDate.now()) {
            dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        }
    }
}
