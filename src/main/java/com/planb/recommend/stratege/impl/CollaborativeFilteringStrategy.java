package com.planb.recommend.stratege.impl;

import com.planb.dao.AdMapper;
import com.planb.entity.Ad;
import com.planb.entity.User;
import com.planb.entity.UserBehavior;
import com.planb.recommend.stratege.RecommendationStrategy;
import com.planb.util.RecommendUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 协同过滤策略
 */
@Component
@RequiredArgsConstructor
public class CollaborativeFilteringStrategy implements RecommendationStrategy {

    private final RecommendUtil recommendUtil;
    private final AdMapper adMapper;

    @Override
    public List<Ad> recommend(User user, int numRecommendations) {
        List<UserBehavior> allUserBehaviors = recommendUtil.getAllUserBehaviors();
        Map<String, List<String>> userAdMap = recommendUtil.getUserAdMap(allUserBehaviors);

        // 计算与当前用户的相似度
        Map<String, Double> userSimilarityMap = new HashMap<>();
        for (String otherUserId : userAdMap.keySet()) {
            if (!otherUserId.equals(user.getId())) {
                double similarity = calculateUserSimilarity(user.getId(), otherUserId, userAdMap);
                userSimilarityMap.put(otherUserId, similarity);
            }
        }

        // 找到最相似的用户
        List<String> similarUsers = userSimilarityMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(numRecommendations)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 推荐这些用户喜欢的广告
        Set<String> recommendedAdIds = new HashSet<>();
        for (String similarUserId : similarUsers) {
            recommendedAdIds.addAll(userAdMap.get(similarUserId));
        }

        // 获取推荐的广告列表
        List<Ad> recommendedAds = recommendedAdIds.stream()
                .map(adMapper::selectById)
                .collect(Collectors.toList());

        return recommendedAds.stream().limit(numRecommendations).collect(Collectors.toList());
    }

    // 计算用户相似度（基于Jaccard相似系数）
    private double calculateUserSimilarity(String userId1, String userId2, Map<String, List<String>> userAdMap) {
        Set<String> user1Ads = new HashSet<>(userAdMap.get(userId1));
        Set<String> user2Ads = new HashSet<>(userAdMap.get(userId2));

        Set<String> intersection = new HashSet<>(user1Ads);
        intersection.retainAll(user2Ads);

        Set<String> union = new HashSet<>(user1Ads);
        union.addAll(user2Ads);

        return (double) intersection.size() / union.size();
    }

}
