package com.wutsi.application.chat.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shared.ui.BottomNavigationBarWidget
import com.wutsi.application.shared.ui.BottomNavigationButton
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

abstract class AbstractEndpoint {
    @Autowired
    protected lateinit var messages: MessageSource

    @Autowired
    protected lateinit var securityContext: SecurityContext

    @Autowired
    protected lateinit var urlBuilder: URLBuilder

    @Autowired
    private lateinit var tracingContext: TracingContext

    @Value("\${wutsi.application.server-url}")
    protected lateinit var serverUrl: String

    @Value("\${wutsi.application.shell-url}")
    protected lateinit var shellUrl: String

    @Value("\${wutsi.application.cash-url}")
    protected lateinit var cashUrl: String

    @Value("\${wutsi.application.store-url}")
    protected lateinit var storeUrl: String

    @Autowired
    protected lateinit var sharedUIMapper: SharedUIMapper

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    protected fun gotoUrl(
        url: String,
        replacement: Boolean? = null,
        parameters: Map<String, String>? = null
    ) = Action(
        type = ActionType.Route,
        url = url,
        replacement = replacement,
        parameters = parameters
    )

    protected fun getText(key: String, args: Array<Any?> = emptyArray()) =
        messages.getMessage(key, args, LocaleContextHolder.getLocale()) ?: key

    protected fun bottomNavigationBar() = BottomNavigationBarWidget(
        model = sharedUIMapper.toBottomNavigationBarModel(
            chatUrl = "",
            shellUrl = shellUrl,
            cashUrl = cashUrl,
            storeUrl = storeUrl,
            urlBuilder = urlBuilder,
            selectedButton = BottomNavigationButton.CHAT
        )
    ).toBottomNavigationBar()
}
