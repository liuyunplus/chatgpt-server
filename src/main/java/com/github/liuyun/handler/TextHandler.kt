package com.github.liuyun.handler

import com.github.liuyun.utils.OpenAiHelper
import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpMessageHandler
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TextHandler : WxMpMessageHandler {

    @Autowired
    private lateinit var openAiHelper: OpenAiHelper

    private var contextMap: MutableMap<String, String> = mutableMapOf()

    override fun handle(wxMessage: WxMpXmlMessage, context: Map<String, Any>,
                        wxMpService: WxMpService, sessionManager: WxSessionManager): WxMpXmlOutMessage {
        val inContent = wxMessage.content
        val outContent = getContext(inContent)
        return WxMpXmlOutMessage.TEXT().content(outContent).fromUser(wxMessage.toUser).toUser(wxMessage.fromUser).build()
    }

    fun getContext(inContent: String): String {
        if (inContent.startsWith("M@")) {
            if (contextMap.containsKey(inContent)) {
                var outContent = contextMap[inContent]
                return outContent!!
            }
            return "内容正在生成，请稍等"
        }
        val randomCode = (10..99).random()
        openAiHelper.fetchContentAsync(inContent) { text ->
            contextMap["M@$randomCode"] = text
        }
        return "正在生成中...，请发送【M@$randomCode】获取结果";
    }

}
