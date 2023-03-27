package com.tencent.wxcloudrun.service


import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import me.chanjar.weixin.mp.api.WxMpMessageHandler
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TextHandler : WxMpMessageHandler {

    @Value("\${apiKey}")
    private lateinit var apiKey: String

    override fun handle(wxMessage: WxMpXmlMessage, context: Map<String, Any>,
                        wxMpService: WxMpService, sessionManager: WxSessionManager): WxMpXmlOutMessage {
        // 接收的消息内容
        val inContent = wxMessage.content
        // 响应的消息内容
        val outContent = getFromOpenAi(inContent)
        // 构造响应消息对象
        return WxMpXmlOutMessage.TEXT().content(outContent).fromUser(wxMessage.toUser).toUser(wxMessage.fromUser).build()
    }

    fun getFromOpenAi(inContent: String): String? {
        val url = "https://api.openai.com/v1/chat/completions"
        val headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer $apiKey")
        val data = """
        {
          "model": "gpt-3.5-turbo",
          "messages": [{"role": "user", "content": "$inContent"}],
          "temperature": 0.7
        }
        """.trimIndent()
        val (_, response, result) = url.httpPost().header(headers).jsonBody(data).responseString()
        return getContent(result.get())
    }

    fun getContent(text:String): String {
        var jsonObj = ObjectMapper().readValue(text, Map::class.java)
        var choices: List<Any> = jsonObj["choices"] as List<Any>
        var item = choices[0] as Map<String, Any>
        var message = item["message"] as Map<String, Any>
        var content = message["content"] as String
        content = content.replace("\n", " ")
        return content
    }

}
