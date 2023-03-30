package com.github.liuyun.controller;

import com.github.liuyun.utils.OpenAiHelper;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * WeChatController
 */
@RestController
public class WeChatController {

    private Logger log = LoggerFactory.getLogger(WeChatController.class);

    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private WxMpMessageRouter wxMpMessageRouter;
    @Autowired
    private OpenAiHelper openAiHelper;

    @GetMapping("test")
    public String test(String msg) {
        return openAiHelper.fetchContent(msg);
    }

    @GetMapping("message")
    public String configAccess(String signature, String timestamp, String nonce, String echostr) {
        // 校验签名
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            // 校验成功原样返回
            return echostr;
        }
        // 校验失败
        return null;
    }

    @PostMapping(value = "message", produces = "application/xml; charset=UTF-8")
    public String handleMessage(@RequestBody String requestBody) {
        log.info("handleMessage调用: " + requestBody);
        // 解析消息体，封装为对象
        WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
        WxMpXmlOutMessage outMessage;
        try {
            // 将消息路由给对应的处理器，获取响应
            outMessage = wxMpMessageRouter.route(inMessage);
        } catch (Exception e) {
            log.error("微信消息路由异常", e);
            outMessage = null;
        }
        System.out.println(outMessage.toXml());
        // 将响应消息转换为xml格式返回
        return outMessage == null ? "" : outMessage.toXml();
    }

}