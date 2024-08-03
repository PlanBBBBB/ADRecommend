package com.planb.service.impl;

import com.planb.constant.AIConstant;
import com.planb.util.AiUtil;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class AITest {

    @Resource
    private AiUtil aiUtil;

    @Test
    public void test() {
        // 初始化客户端
        String apiKey = "xxx";
        ClientV4 client = new ClientV4.Builder(apiKey).build();

        // 构造请求
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "作为一名营销专家，请为智谱开放平台创作一个吸引人的slogan");
        messages.add(chatMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);

        System.out.println("model output:" + invokeModelApiResp.getData().getChoices().get(0));

    }

    @Test
    public void testWithResult() {
        String userId = "1";
        String num = "4";
        // 构建AI请求消息
        String userMessage = "请对编号为" + userId + "的用户推荐" + num + "个广告";
        String result = aiUtil.doSyncStableRequest(AIConstant.SYSTEM_MESSAGE, userMessage);
        String[] split = result.split(",");
        System.out.println(result);
        if (split.length != Integer.parseInt(num)) {
            throw new RuntimeException(AIConstant.ERROR_MESSAGE);
        }

    }

    @Test
    public void testPro() {

        // 构建AI请求消息
        String userMessage = "新增用户行为信息：\n" +
                "【【【\n" +
                "behaviorId|userId|adId|action|created\n" +
                "1|1|1|100001|2024-06-26 11:01:13\n" +
                "2|2|1|100001|2024-06-26 11:04:18\n" +
                "3|3|1|100002|2024-06-26 11:04:18\n" +
                "4|2|1|100001|2024-06-26 11:32:59\n" +
                "5|4|1|100002|2024-07-18 22:02:45\n" +
                "6|2|3|100001|2024-07-18 22:03:01\n" +
                "7|4|3|100002|2024-07-18 22:05:14\n" +
                "8|6|5|100001|2024-07-18 22:05:30\n" +
                "】】】";
        String result = aiUtil.doSyncStableRequest(AIConstant.SYSTEM_MESSAGE, userMessage);
        System.out.println(result);

    }

}
