package com.planb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class AdRecommendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdRecommendApplication.class, args);
        log.info("AdRecommend项目启动成功.......");
    }

}
