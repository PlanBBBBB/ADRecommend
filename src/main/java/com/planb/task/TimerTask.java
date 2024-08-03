package com.planb.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.planb.constant.AIConstant;
import com.planb.dao.AdMapper;
import com.planb.dao.UserBehaviorMapper;
import com.planb.dao.UserMapper;
import com.planb.entity.Ad;
import com.planb.entity.User;
import com.planb.entity.UserBehavior;
import com.planb.util.AiUtil;
import com.planb.util.CsvUtil;
import com.planb.util.MailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.planb.constant.EmailConstant.ADMIN_EMAIL;

/**
 * @author PlanB
 * 定时器 用于采集用户行为，用户信息，广告信息给予AI补充信息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TimerTask {

    private final UserBehaviorMapper userBehaviorMapper;
    private final UserMapper userMapper;
    private final AdMapper adMapper;
    private final AiUtil aiUtil;

    /**
     * 采集用户行为信息 - 每天凌晨12点执行一次
     */
    @Scheduled(cron = "0 0 * * *")
    public void behaviorsCollectionTask() {
        log.info("开始采集用户行为信息");
        // 取一天内新增的行为进行补充
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<UserBehavior> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(UserBehavior::getCreated, now.minusDays(1), now);
        List<UserBehavior> behaviorList = userBehaviorMapper.selectList(queryWrapper);
        if (behaviorList.isEmpty()) {
            log.info("{}至{}没有新增的行为信息", now.minusDays(1), now);
            return;
        }
        // 采集行为信息
        String behaviorsToCsvCsv = CsvUtil.behaviorsToCsv(behaviorList);
        String userMessage = "新增用户行为信息：\n" +
                "【【【\n" +
                behaviorsToCsvCsv +
                "】】】";
        String result = aiUtil.doSyncStableRequest(AIConstant.SYSTEM_MESSAGE, userMessage);
        if (!AIConstant.SYSTEM_MESSAGE_RESULT.equals(result)) {
            // 邮件告警
            String body = "AI采集行为信息错误，请及时查看！\n";
            try {
                MailUtil.sendEmail(ADMIN_EMAIL, AIConstant.SUBJECT, body);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException("AI返回结果错误");
        }
        log.info("结束采集用户行为信息");
    }

    /**
     * 采集用户信息 - 每天凌晨1点执行一次
     */
    @Scheduled(cron = "0 1 * * *")
    public void usersCollectionTask() {
        log.info("开始采集用户信息");
        // 取一天内新增的用户进行补充
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(User::getCreated, now.minusDays(1), now);
        List<User> userList = userMapper.selectList(queryWrapper);
        if (userList.isEmpty()) {
            log.info("{}至{}没有新增的用户信息", now.minusDays(1), now);
            return;
        }
        // 采集用户信息
        String usersToCsv = CsvUtil.usersToCsv(userList);
        String userMessage = "新增用户行为信息：\n" +
                "【【【\n" +
                usersToCsv +
                "】】】";
        String result = aiUtil.doSyncStableRequest(AIConstant.SYSTEM_MESSAGE, userMessage);
        if (!AIConstant.SYSTEM_MESSAGE_RESULT.equals(result)) {
            // 邮件告警
            String body = "AI采集用户信息错误，请及时查看！\n";
            try {
                MailUtil.sendEmail(ADMIN_EMAIL, AIConstant.SUBJECT, body);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException("AI返回结果错误");
        }
        log.info("结束采集用户信息");
    }

    /**
     * 采集广告信息 - 每天凌晨2点执行一次
     */
    @Scheduled(cron = "0 2 * * *")
    public void adsCollectionTask() {
        log.info("开始采集广告信息");
        // 取一天内新增的广告进行补充
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Ad> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Ad::getCreated, now.minusDays(1), now);
        List<Ad> adList = adMapper.selectList(queryWrapper);
        if (adList.isEmpty()) {
            log.info("{}至{}没有新增的广告信息", now.minusDays(1), now);
            return;
        }
        // 采集广告信息
        String adsToCsv = CsvUtil.adToCsv(adList);
        String userMessage = "新增用户行为信息：\n" +
                "【【【\n" +
                adsToCsv +
                "】】】";
        String result = aiUtil.doSyncStableRequest(AIConstant.SYSTEM_MESSAGE, userMessage);
        if (!AIConstant.SYSTEM_MESSAGE_RESULT.equals(result)) {
            // 邮件告警
            String body = "AI采集广告信息错误，请及时查看！\n";
            try {
                MailUtil.sendEmail(ADMIN_EMAIL, AIConstant.SUBJECT, body);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException("AI返回结果错误");
        }
        log.info("结束采集广告信息");
    }
}
