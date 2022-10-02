package com.wutsi.application.chat.endpoint.message.screen

import com.wutsi.application.chat.endpoint.AbstractQuery
import com.wutsi.application.chat.endpoint.Page
import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.StringUtil
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Chat
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.platform.account.WutsiAccountApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/messages")
class MessagesScreen(
    private val accountApi: WutsiAccountApi
) : AbstractQuery() {
    @PostMapping
    fun index(@RequestParam(name = "recipient-id") recipientId: Long): Widget {
        val me = securityContext.currentAccount()
        val recipient = accountApi.getAccount(recipientId).account
        return Screen(
            id = Page.CHAT_MESSAGE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = recipient.displayName ?: ""
            ),
            child = Chat(
                roomId = recipientId.toString(),
                userId = me.id.toString(),
                userFirstName = StringUtil.firstName(me.displayName),
                userLastName = StringUtil.lastName(me.displayName),
                userPictureUrl = me.pictureUrl,
                sendMessageUrl = "$serverUrl/commands/send",
                fetchMessageUrl = "$serverUrl/messages/fetch?recipient-id=$recipientId"
            )
        ).toWidget()
    }
}
