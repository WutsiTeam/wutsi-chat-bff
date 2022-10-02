package com.wutsi.application.chat.endpoint.message

import com.wutsi.application.chat.endpoint.AbstractQuery
import com.wutsi.application.chat.endpoint.message.dto.ChatMessageDto
import com.wutsi.application.chat.endpoint.message.dto.ChatUserDto
import com.wutsi.application.shared.service.StringUtil
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.chat.WutsiChatApi
import com.wutsi.platform.chat.dto.SearchMessageRequest
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class FetchMessageController(
    private val accountApi: WutsiAccountApi,
    private val chatApi: WutsiChatApi,
    private val logger: KVLogger
) : AbstractQuery() {
    @PostMapping("/messages/fetch")
    fun index(@RequestParam("recipient-id") recipientId: Long): List<ChatMessageDto> {
        val messages = chatApi.searchMessages(
            request = SearchMessageRequest(
                accountId1 = recipientId,
                accountId2 = securityContext.currentAccountId(),
                limit = 50
            )
        ).messages
        logger.add("message_count", messages.size)
        if (messages.isEmpty()) {
            return emptyList()
        }

        val participants = accountApi.searchAccount(
            request = SearchAccountRequest(
                ids = listOf(recipientId, securityContext.currentAccountId())
            )
        ).accounts.associateBy { it.id }
        return messages.map {
            val author = participants[it.senderId]

            ChatMessageDto(
                id = it.referenceId,
                createdAt = it.timestamp,
                roomId = recipientId.toString(),
                text = it.text,
                author = ChatUserDto(
                    id = author?.id.toString(),
                    firstName = author?.displayName?.let { StringUtil.firstName(author.displayName) },
                    lastName = author?.displayName?.let { StringUtil.lastName(author.displayName) },
                    imageUrl = author?.pictureUrl
                )
            )
        }
    }
}
