package com.planb.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class AIManagerTest {

    @Resource
    private AIManager aiManager;

    @Test
    void doChat() {
        String responce = aiManager.doChat("帮我写一个冒泡排序的代码使用Java语言");
        System.out.println(responce);
    }
}