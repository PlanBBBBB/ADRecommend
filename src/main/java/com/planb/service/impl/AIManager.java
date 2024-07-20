package com.planb.service.impl;

import com.planb.config.BaseUnCheckedException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AIManager {

    @Resource
    private YuCongMingClient yucongmingClient;

    /**
     * AI 对话
     */
    public String doChat(String message) {
        // 构造请求参数
        DevChatRequest devChatRequest = new DevChatRequest();
        // 模型id，尾后加L，转成long类型
        devChatRequest.setModelId(1654785040361893889L);
        devChatRequest.setMessage(message);
        // 获取响应结果
        BaseResponse<DevChatResponse> response = yucongmingClient.doChat(devChatRequest);
        // 如果响应结果为null，就抛出系统异常，提示AI响应错误
        if (response == null) {
            throw new BaseUnCheckedException("AI响应错误");
        }
        return response.getData().getContent();
    }
}
