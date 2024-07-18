package com.planb.util.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 全局IO密集型线程池
 */
@Slf4j
public class GlobalThreadPool {
    private static volatile ExecutorService instance;

    // 私有构造方法，防止外部实例化
    private GlobalThreadPool() {
    }

    // 获取全局唯一线程池实例
    public static ExecutorService getInstance() {
        // 双重检查锁定，确保线程安全
        if (instance == null) {
            synchronized (GlobalThreadPool.class) {
                if (instance == null) {
                    instance = createThreadPool();
                }
            }
        }
        return instance;
    }

    // 创建线程池
    private static ExecutorService createThreadPool() {
        int corePoolSize = 8; // 核心线程数（IO密集型为CPU核数*4）
        int maximumPoolSize = 10; // 最大线程数（IO密集型为CPU核数*5）
        long keepAliveTime = 60L; // 空闲线程存活时间（秒）
        TimeUnit unit = TimeUnit.SECONDS; // 时间单位
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(10); // 任务队列
        // 使用自定义线程工厂
        ThreadFactory threadFactory = new CustomThreadFactory(
                "GlobalPoolThread",   // baseName
                false,                // daemon
                Thread.NORM_PRIORITY, // priority
                null,                 // thread group
                (t, e) -> log.error("线程异常", e), // exception handler
                0                     // stack size
        );
        RejectedExecutionHandler handler = new CustomRejectedExecutionHandler(); // 自定义拒绝策略：用调用者所在的线程来执行任务，发送邮件，记录日志
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    // 关闭线程池
    public static void shutdown() {
        if (instance != null) {
            instance.shutdown();
            try {
                if (!instance.awaitTermination(60, TimeUnit.SECONDS)) {
                    instance.shutdownNow();
                    if (!instance.awaitTermination(60, TimeUnit.SECONDS)) {
                        log.error("线程池未能正常关闭");
                    }
                }
            } catch (InterruptedException ie) {
                instance.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
