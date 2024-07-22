package com.planb.recommend.stratege.impl;

import com.planb.dao.AdMapper;
import com.planb.entity.Ad;
import com.planb.entity.User;
import com.planb.entity.UserBehavior;
import com.planb.recommend.stratege.RecommendationStrategy;
import com.planb.util.RecommendUtil;
import com.planb.util.SVDRecommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于SVD算法的推荐策略
 */
@Component
@RequiredArgsConstructor
public class SVDStrategy implements RecommendationStrategy {

    private final RecommendUtil recommendUtil;
    private final AdMapper adMapper;

    @Override
    public List<Ad> recommend(User user, int numRecommendations) {
        List<UserBehavior> allUserBehaviors = recommendUtil.getAllUserBehaviors();
        Map<String, List<String>> userAdMap = recommendUtil.getUserAdMap(allUserBehaviors);

        List<String> userIds = new ArrayList<>(userAdMap.keySet());
        List<String> adIds = recommendUtil.getAllAds().stream()
                .map(Ad::getId)
                .collect(Collectors.toList());

        double[][] data = buildUserAdMatrix(userAdMap, userIds, adIds);
        SVDRecommendation svdRec = new SVDRecommendation(data, userIds, adIds);
        List<String> recommendedAdIds = svdRec.recommend(user.getId(), numRecommendations);

        return recommendedAdIds.stream()
                .map(adMapper::selectById)
                .collect(Collectors.toList());
    }

    // 构建用户-广告矩阵
    private double[][] buildUserAdMatrix(Map<String, List<String>> userAdMap, List<String> userIds, List<String> adIds) {
        double[][] data = new double[userIds.size()][adIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            for (int j = 0; j < adIds.size(); j++) {
                String userId = userIds.get(i);
                String adId = adIds.get(j);
                data[i][j] = userAdMap.getOrDefault(userId, Collections.emptyList()).contains(adId) ? 1 : 0;
            }
        }
        return data;
    }
}
