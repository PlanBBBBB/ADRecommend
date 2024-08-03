package com.planb.config;


import com.planb.service.GlobalService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * SpringBoot 启动后后置操作
 */
@Component
@RequiredArgsConstructor
public class ApplicationRunnerImpl implements ApplicationRunner {

    private final GlobalService globalService;

    @Override
    public void run(ApplicationArguments args){
        // 初始化字典数据
        globalService.initDict();
        // 初始化广告数据
        globalService.initAd();
        // 初始化推荐规则引擎
        globalService.initEngine();
    }

}
