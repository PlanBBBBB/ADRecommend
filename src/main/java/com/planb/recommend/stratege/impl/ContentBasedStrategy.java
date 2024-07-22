package com.planb.recommend.stratege.impl;

import com.planb.entity.Ad;
import com.planb.entity.User;
import com.planb.recommend.stratege.RecommendationStrategy;
import com.planb.util.RecommendUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于内容推荐
 */
@Component
@RequiredArgsConstructor
public class ContentBasedStrategy implements RecommendationStrategy {

    private final RecommendUtil recommendUtil;

    @Override
    public List<Ad> recommend(User user, int numRecommendations) {
        List<String> userInterests = recommendUtil.getUserInterests(user);
        List<Ad> allAds = recommendUtil.getAllAds();

        // 为每个广告计算相似度
        Map<Ad, Double> adSimilarityMap = new HashMap<>();
        for (Ad ad : allAds) {
            List<String> adKeywords = recommendUtil.getAdKeywords(ad);
            double similarity = recommendUtil.calculateSimilarity(userInterests, adKeywords);
            adSimilarityMap.put(ad, similarity);
        }

        // 按相似度排序并获取最相似的广告
        return adSimilarityMap.entrySet().stream()
                .sorted(Map.Entry.<Ad, Double>comparingByValue().reversed())
                .limit(numRecommendations)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
