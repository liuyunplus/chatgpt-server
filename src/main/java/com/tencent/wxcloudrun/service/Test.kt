package com.tencent.wxcloudrun.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost


fun main() {
    getFromOpenAi("帮我取3个中文名")
}

fun getFromOpenAi(inContent: String): String? {
    val apiKey = "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"
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
    return content
}