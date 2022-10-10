package com.wutsi.application.chat.endpoint.message.screen

import com.wutsi.application.chat.endpoint.AbstractEndpointTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class MessagesScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/messages?recipient-id=11"
    }

    @Test
    fun messages() {
        assertEndpointEquals("/screens/messages/index.json", url)
    }

    @Test
    fun text() {
        assertEndpointEquals("/screens/messages/text.json", "$url&text=Hello&url=https://www.google.ca")
    }
}
