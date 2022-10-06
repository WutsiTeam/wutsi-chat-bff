package com.wutsi.application.chat.endpoint.message.screen

import com.wutsi.application.chat.endpoint.AbstractQuery
import com.wutsi.application.chat.endpoint.Page
import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.StringUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Chat
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/messages")
class MessagesScreen(
    private val accountApi: WutsiAccountApi,
    private val env: Environment,
    @Value("\${wutsi.chat.rtm-url}") private val rtmUrl: String
) : AbstractQuery() {
    @PostMapping
    fun index(@RequestParam(name = "recipient-id") recipientId: Long): Widget {
        val sender = securityContext.currentAccount()
        val recipient = accountApi.getAccount(recipientId).account
        val roomId = DigestUtils.md5Hex(
            listOf(sender.id, recipientId).sorted().joinToString(",")
        )

        return Screen(
            id = Page.CHAT_MESSAGE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = recipient.displayName ?: "",
                actions = listOf(
                    IconButton(
                        icon = Theme.ICON_CANCEL,
                        action = Action(
                            type = ActionType.Route,
                            url = "route:/~"
                        )
                    )
                )
            ),
            child = Chat(
                roomId = roomId,
                userId = sender.id.toString(),
                userFirstName = StringUtil.firstName(sender.displayName),
                userLastName = StringUtil.lastName(sender.displayName),
                userPictureUrl = sender.pictureUrl,
                recipientUserId = recipientId.toString(),
                fetchMessageUrl = "$serverUrl/messages/fetch?recipient-id=$recipientId",
                rtmUrl = rtmUrl,
                language = sender.language,
                sentMessageBackground = Theme.COLOR_PRIMARY,
                sentMessageTextColor = Theme.COLOR_WHITE,
                receivedMessageTextColor = Theme.COLOR_BLACK,
                receivedMessageBackground = Theme.COLOR_GRAY_LIGHT,
                fontSize = Theme.TEXT_SIZE_DEFAULT,
                showUserNames = false,
                showUserAvatars = true
            )
        ).toWidget()
    }
}
