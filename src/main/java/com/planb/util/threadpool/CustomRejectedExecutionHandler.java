package com.planb.util.threadpool;

import com.planb.config.BaseUnCheckedException;
import com.planb.util.MailUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static com.planb.constant.EmailConstant.ADMIN_EMAIL;

/**
 * 自定义拒绝策略
 */
@Slf4j
public class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        // 打印日志
        log.error("任务 {} 被拒绝。当前线程池状态: 核心线程数: {}, 活跃线程数: {}, 完成任务数: {}, 任务总数: {}",
                r.toString(),
                executor.getCorePoolSize(),
                executor.getActiveCount(),
                executor.getCompletedTaskCount(),
                executor.getTaskCount());

        // 发送邮件通知管理员
        try {
            sendEmailToAdmin(r, executor);
        } catch (Exception e) {
            throw new BaseUnCheckedException("发送邮件通知管理员失败", 500);
        }

        // 用调用者所在的线程来执行任务
        if (!executor.isShutdown()) {
            r.run();
        }
    }

    private void sendEmailToAdmin(Runnable r, ThreadPoolExecutor executor) throws Exception {
        // 实现发送邮件通知的逻辑
        String subject = "任务被拒绝通知";
        String body = String.format("任务 %s 被拒绝。当前线程池状态: 核心线程数: %d, 活跃线程数: %d, 完成任务数: %d, 任务总数: %d",
                r.toString(),
                executor.getCorePoolSize(),
                executor.getActiveCount(),
                executor.getCompletedTaskCount(),
                executor.getTaskCount());

        // 使用邮件服务发送邮件
        MailUtil.sendEmail(ADMIN_EMAIL, subject, body);
    }
}
