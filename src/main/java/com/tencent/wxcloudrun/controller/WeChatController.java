package com.tencent.wxcloudrun.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.wxcloudrun.config.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;

/**
 * counter控制器
 */
@RestController
public class WeChatController {

    @PostMapping(value = "/api/msg")
    String get(@RequestBody Map<String, Object> param) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(param);
        System.out.println(jsonString);
        param.put("CreateTime", System.currentTimeMillis() / 1000);
        param.put("Content", "欢迎来到我的博客");
        param.remove("MsgId");
        return objectMapper.writeValueAsString(param);
    }

}