package com.planb.config;

import com.planb.util.threadpool.GlobalThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 全局线程池关闭处理类
 */
@Component
@Slf4j
public class GlobalThreadPoolManager {

    @PreDestroy
    public void shutdownThreadPool() {
        log.info("Spring 容器关闭前执行线程池关闭...");
        GlobalThreadPool.shutdown();
    }
}
