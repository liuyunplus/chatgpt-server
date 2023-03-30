package com.github.liuyun.utils

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.jayway.jsonpath.JsonPath
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class OpenAiHelper {

    @Value("\${apiKey}")
    private lateinit var apiKey: String

    fun fetchContent(inContent: String): String {
        val url = "https://api.openai.com/v1/chat/completions"
        val headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer $apiKey")
        val data = """
        {
          "model": "gpt-3.5-turbo",
          "messages": [{"role": "user", "content": "$inContent"}],
          "temperature": 0.7
        }
        """.trimIndent()
//        val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", 7890))
//        FuelManager.instance.proxy = proxy
        val (v1, v2, result) = url.httpPost().timeoutRead(60000).header(headers).jsonBody(data).responseString()
        val content = JsonPath.read<String>(result.get(), "\$.choices[0].message.content")
        return content
    }

    fun fetchContentAsync(inContent: String, callback: (result: String) -> Unit) {
        GlobalScope.launch {
            var outContent = fetchContent(inContent)
            callback(outContent)
        }
    }

}

