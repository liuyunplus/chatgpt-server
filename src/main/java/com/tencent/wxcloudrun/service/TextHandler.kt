package com.tencent.wxcloudrun.service


import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.jayway.jsonpath.JsonPath
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpMessageHandler
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TextHandler : WxMpMessageHandler {

    @Value("\${apiKey}")
    private lateinit var apiKey: String

    private var contextMap: MutableMap<String, String> = mutableMapOf()

    override fun handle(wxMessage: WxMpXmlMessage, context: Map<String, Any>,
                        wxMpService: WxMpService, sessionManager: WxSessionManager): WxMpXmlOutMessage {
        val inContent = wxMessage.content
        val outContent = getContext(inContent)
        return WxMpXmlOutMessage.TEXT().content(outContent).fromUser(wxMessage.toUser).toUser(wxMessage.fromUser).build()
    }

    fun getFromOpenAi(inContent: String, callback: (result: String) -> Unit) {
        GlobalScope.launch {
            val apiKey = System.getenv("OPENAI_KEY")
            val url = "https://api.openai.com/v1/chat/completions"
            val headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer $apiKey")
            val data = """
            {
              "model": "gpt-3.5-turbo",
              "messages": [{"role": "user", "content": "$inContent"}],
              "temperature": 0.7
            }
            """.trimIndent()
//            val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", 7890))
//            FuelManager.instance.proxy = proxy
            val (v1, v2, result) = url.httpPost().header(headers).jsonBody(data).responseString()
            val content = JsonPath.read<String>(result.get(), "\$.choices[0].message.content")
            callback(content)
        }
    }

    fun getContext(inContent: String): String {
        if (inContent.startsWith("M@")) {
            if (contextMap.containsKey(inContent)) {
                var outContent = contextMap[inContent]
                contextMap.remove(inContent)
                return outContent!!
            }
            return "内容正在生成，请稍等"
        }
        val randomCode = (10..99).random()
        getFromOpenAi(inContent) { text ->
            contextMap["M@$randomCode"] = text
        }
        return "正在生成中...，请发送【M@$randomCode】获取结果";
    }

}
