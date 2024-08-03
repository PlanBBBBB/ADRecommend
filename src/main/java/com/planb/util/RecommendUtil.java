package com.planb.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.planb.constant.RedisConstant;
import com.planb.dao.AdMapper;
import com.planb.dao.UserBehaviorMapper;
import com.planb.entity.Ad;
import com.planb.entity.User;
import com.planb.entity.UserBehavior;
import com.planb.util.threadpool.GlobalThreadPool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author PlanB
 * 推荐工具类
 */
@Service
@RequiredArgsConstructor
public class RecommendUtil {

    private final AdMapper adMapper;
    private final UserBehaviorMapper userBehaviorMapper;
    private final ExecutorService executor = GlobalThreadPool.getInstance();


    /**
     * 获取广告列表（从缓存/数据库中获取）
     */
    public List<Ad> getAllAds() {
        String jsonStr = RedisUtil.get(RedisConstant.AD);
        List<Ad> list = JSONArray.parseArray(jsonStr, Ad.class);
        if (ValidateUtil.isBlank(list)) {
            List<Ad> list1 = adMapper.selectList(null);
            CompletableFuture.runAsync(() -> {
                JSONArray from = JSONArray.from(list1);
                RedisUtil.set(RedisConstant.AD, from.toJSONString(JSONWriter.Feature.WriteMapNullValue));
            }, executor);
            return list1;
        }
        return list;
    }

    /**
     * 计算相似度
     */
    public double calculateSimilarity(List<String> userInterests, List<String> adKeywords) {
        Set<String> commonKeywords = new HashSet<>(userInterests);
        commonKeywords.retainAll(adKeywords);
        return (double) commonKeywords.size() / (userInterests.size() + adKeywords.size());
    }

    /**
     * 获取所有用户的行为数据
     */
    public List<UserBehavior> getAllUserBehaviors() {
        return userBehaviorMapper.selectList(null);
    }

    /**
     * 获取用户的兴趣列表
     */
    public List<String> getUserInterests(User user) {
        return Arrays.asList(user.getInterest().split(","));
    }

    /**
     * 获取广告的关键词列表
     */
    public List<String> getAdKeywords(Ad ad) {
        return Arrays.asList(ad.getKeyWords().split(","));
    }

    /**
     * 获取用户广告映射
     */
    public Map<String, List<String>> getUserAdMap(List<UserBehavior> allUserBehaviors) {
        Map<String, List<String>> userAdMap = new HashMap<>();
        for (UserBehavior behavior : allUserBehaviors) {
            userAdMap.computeIfAbsent(behavior.getUserId(), k -> new ArrayList<>()).add(behavior.getAdId());
        }
        return userAdMap;
    }

    /**
     * 增加推荐结果的多样性和探索机制
     */
    public List<Ad> addDiversityAndExploration(List<Ad> recommendedAds, int numRecommendations) {
        List<UserBehavior> behaviorList = getAllUserBehaviors();
        // 初始化已看过的广告类型集合
        Set<String> seenCategories = new HashSet<>();
        // 最终返回的广告列表
        List<Ad> diverseAds = new ArrayList<>();
        Random random = new Random();

        // 计算用户对每个广告类型的兴趣度
        Map<String, Double> categoryInterestMap = calculateCategoryInterest(behaviorList);

        for (Ad ad : recommendedAds) {
            if (diverseAds.size() >= numRecommendations) {
                break;
            }
            String type = ad.getType();
            if (!seenCategories.contains(type)) {
                diverseAds.add(ad);
                seenCategories.add(type);
            } else {
                // 根据用户对该类别的兴趣度动态调整探索概率
                double exploreProbability = getExploreProbability(type, categoryInterestMap);
                if (random.nextDouble() < exploreProbability) {
                    diverseAds.add(ad);
                }
            }
        }

        // 如果多样性不够，填补推荐列表
        int i = 0;
        while (diverseAds.size() < numRecommendations && i < recommendedAds.size()) {
            if (!diverseAds.contains(recommendedAds.get(i))) {
                diverseAds.add(recommendedAds.get(i));
            }
            i++;
        }
        // 增加曝光次数
        CompletableFuture.runAsync(() -> diverseAds.forEach(ad -> {
            LambdaUpdateWrapper<Ad> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(Ad::getExposureCount, ad.getExposureCount() + 1);
            adMapper.update(ad, wrapper);
        }), executor);
        return diverseAds;
    }

    // 根据用户对广告类别的兴趣度动态调整探索概率
    private double getExploreProbability(String category, Map<String, Double> categoryInterestMap) {
        double interest = categoryInterestMap.getOrDefault(category, 0.0);
        // 假设兴趣度越高，探索概率越低，最低为10%
        return Math.max(0.1, 1.0 - interest);
    }

    // 计算用户对每个广告类型的兴趣度
    private Map<String, Double> calculateCategoryInterest(List<UserBehavior> behaviorList) {
        Map<String, Double> categoryInterestMap = new HashMap<>();
        Map<String, Integer> categoryCountMap = new HashMap<>();

        for (UserBehavior behavior : behaviorList) {
            String adId = behavior.getAdId();
            Ad ad = adMapper.selectById(adId);
            String category = ad.getType();
            categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
            categoryInterestMap.put(entry.getKey(), entry.getValue() / (double) behaviorList.size());
        }

        return categoryInterestMap;
    }
}
