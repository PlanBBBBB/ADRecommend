package com.planb.recommend.stratege.impl;

import com.planb.entity.Ad;
import com.planb.entity.User;
import com.planb.recommend.stratege.RecommendationStrategy;
import com.planb.util.RecommendUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 位置和曝光率推荐
 */
@Component
@RequiredArgsConstructor
public class PositionAndExposureStrategy implements RecommendationStrategy {

    private final RecommendUtil recommendUtil;

    @Override
    public List<Ad> recommend(User user, int numRecommendations) {
        List<String> userInterests = recommendUtil.getUserInterests(user);
        List<Ad> allAds = recommendUtil.getAllAds();
        double maxPosition = allAds.stream().mapToDouble((ad) -> Double.parseDouble(ad.getPosition())).max().orElse(1.0);

        // 为每个广告计算综合权重
        Map<Ad, Double> adWeightMap = new HashMap<>();
        for (Ad ad : allAds) {
            List<String> adKeywords = recommendUtil.getAdKeywords(ad);
            double similarity = recommendUtil.calculateSimilarity(userInterests, adKeywords);
            String position = ad.getPosition();
            double positionWeight = getPositionWeight(Double.parseDouble(position), maxPosition);
            double exposureWeight = getExposureWeight(ad.getExposureCount());
            double totalWeight = similarity * positionWeight * exposureWeight;
            adWeightMap.put(ad, totalWeight);
        }

        // 按综合权重排序并获取最高权重的广告
        return adWeightMap.entrySet().stream()
                .sorted(Map.Entry.<Ad, Double>comparingByValue().reversed())
                .limit(numRecommendations)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 计算位置权重
    private double getPositionWeight(double position, double maxPosition) {
        // 线性归一化，位置值越小权重越高
        return 1.0 - (position / maxPosition);
    }

    // 计算曝光权重
    private double getExposureWeight(int exposureCount) {
        // 曝光频次控制，假设曝光次数越多权重越低
        return 1.0 / (1.0 + Math.log(1 + exposureCount));
    }
}
