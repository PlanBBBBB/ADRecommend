package com.planb.recommend.stratege;

import com.planb.entity.Ad;
import com.planb.entity.User;

import java.util.List;

/**
 * @author PlanB
 * 推荐策略
 */
public interface RecommendationStrategy {
    List<Ad> recommend(User user, int numRecommendations);
}
