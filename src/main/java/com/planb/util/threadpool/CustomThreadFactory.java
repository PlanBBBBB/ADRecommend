package com.planb.util.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程工厂
 */
public class CustomThreadFactory implements ThreadFactory {
    private final String baseName;
    private final boolean daemon;
    private final int priority;
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final Thread.UncaughtExceptionHandler exceptionHandler;
    private final long stackSize;

    public CustomThreadFactory(String baseName, boolean daemon, int priority, ThreadGroup group, Thread.UncaughtExceptionHandler exceptionHandler, long stackSize) {
        this.baseName = baseName;
        this.daemon = daemon;
        this.priority = priority;
        this.group = (group != null) ? group : Thread.currentThread().getThreadGroup();
        this.exceptionHandler = exceptionHandler;
        this.stackSize = stackSize;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread t = new Thread(group, r, baseName + "-" + threadNumber.getAndIncrement(), stackSize);
        t.setDaemon(daemon);
        t.setPriority(priority);
        if (exceptionHandler != null) {
            t.setUncaughtExceptionHandler(exceptionHandler);
        }
        return t;
    }
}

