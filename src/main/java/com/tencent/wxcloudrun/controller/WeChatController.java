package com.tencent.wxcloudrun.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.wxcloudrun.config.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * counter控制器
 */
@RestController
public class WeChatController {

    @PostMapping(value = "/api/msg")
    ApiResponse get(@RequestBody Object param) throws JsonProcessingException {
      ObjectMapper objectMapper = new ObjectMapper();
      String jsonString = objectMapper.writeValueAsString(param);
      System.out.println(jsonString);
        return ApiResponse.ok(1);
    }

}