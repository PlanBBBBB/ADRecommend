package com.planb.service.impl;

import com.planb.config.BaseUnCheckedException;
import com.planb.constant.ErrorConstant;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * RedisLimiter 限流
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     *
     * @param key 限流key（区分不同的限流器，比如不同的用户id应该分别统计）
     */
    public void doRateLimit(String key) {
        // 创建一个限流器，每秒最多访问2次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // 限流器的统计规则（每秒2个请求；连续的请求，最多只能有1个请求被允许通过）
        // RateType.OVERALL表示速率限制作用于整个令牌桶，即限制所有请求的速率
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当来了一个操作，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw new BaseUnCheckedException(ErrorConstant.TOO_MANY_REQUEST);
        }
    }

}
