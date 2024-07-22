package com.planb.recommend.stratege.impl;

import com.planb.entity.Ad;
import com.planb.entity.User;
import com.planb.recommend.stratege.RecommendationStrategy;

import java.util.List;

/**
 * 基于AI的推荐策略
 */
public class AIRecommendationStrategy implements RecommendationStrategy {
    @Override
    public List<Ad> recommend(User user, int numRecommendations) {
        return null;
    }
}
