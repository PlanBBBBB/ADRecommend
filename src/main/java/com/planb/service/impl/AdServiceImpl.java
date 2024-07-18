package com.planb.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.planb.constant.RedisConstant;
import com.planb.dao.AdMapper;
import com.planb.dao.UserBehaviorMapper;
import com.planb.dto.ad.AddAdDto;
import com.planb.dto.ad.PageAdDto;
import com.planb.dto.ad.UpAdDto;
import com.planb.entity.Ad;
import com.planb.entity.User;
import com.planb.entity.UserBehavior;
import com.planb.security.LoginUser;
import com.planb.service.IAdService;
import com.planb.util.DictUtil;
import com.planb.util.RedisUtil;
import com.planb.util.SVDRecommendation;
import com.planb.util.ValidateUtil;
import com.planb.util.threadpool.GlobalThreadPool;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements IAdService {

    private final AdMapper adMapper;
    private final UserBehaviorMapper userBehaviorMapper;
    private final ExecutorService executor = GlobalThreadPool.getInstance();

    @Override
    public Ad getAdById(String id) {
        return adMapper.selectById(id);
    }

    @Override
    public void add(AddAdDto dto) {
        CompletableFuture.runAsync(() -> {
            Ad ad = new Ad();
            BeanUtils.copyProperties(dto, ad);
            adMapper.insert(ad);
            this.storeAdToRedis();
        }, executor);
    }

    @Override
    public void update(UpAdDto dto) {
        CompletableFuture.runAsync(() -> {
            Ad ad = adMapper.selectById(dto.getId());
            BeanUtils.copyProperties(dto, ad);
            adMapper.updateById(ad);
            this.storeAdToRedis();
        }, executor);
    }

    @Override
    public void delete(String id) {
        CompletableFuture.runAsync(() -> {
            Ad ad = adMapper.selectById(id);
            ad.setIsValid("0");
            adMapper.updateById(ad);
            this.storeAdToRedis();
        }, executor);
    }

    @Override
    public void storeAdToRedis() {
        RedisUtil.delete(RedisConstant.AD);
        LambdaQueryWrapper<Ad> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Ad::getIsValid, 1);
        List<Ad> adList = adMapper.selectList(queryWrapper);
        JSONArray from = JSONArray.from(adList);
        RedisUtil.set(RedisConstant.AD, from.toJSONString(JSONWriter.Feature.WriteMapNullValue));
    }


    @Override
    public IPage<Ad> pageAd(PageAdDto dto) {
        LambdaQueryWrapper<Ad> lqw = new LambdaQueryWrapper<>();
        lqw.ge(Strings.isNotEmpty(dto.getStartTime()), Ad::getStartTime, dto.getStartTime())
                .le(Strings.isNotEmpty(dto.getEndTime()), Ad::getEndTime, dto.getEndTime())
                .eq(Strings.isNotEmpty(dto.getIsValid()), Ad::getIsValid, dto.getIsValid());
        IPage<Ad> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        adMapper.selectPage(page, lqw);
        List<Ad> records = page.getRecords();
        for (Ad ad : records) {
            String keyWords = ad.getKeyWords();
            String isValid = ad.getIsValid();
            String type = ad.getType();
            ad.setKeyWords(DictUtil.changeDict(keyWords));
            ad.setType(DictUtil.changeDict(type));
            ad.setIsValid(isValid.equals("1") ? "有效" : "无效");
        }
        return page;
    }

    @Override
    public List<Ad> recommend(int numRecommendations) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        User user = loginUser.getUser();

        String engine = RedisUtil.get(RedisConstant.RECOMMEND_ENGINE);
        List<Ad> recommendedAds;
        switch (engine) {
            case "content":
                //基于内容推荐算法
                recommendedAds = recommendAdsByContent(user, numRecommendations);
                break;
            case "coordinatedFiltering":
                //基于协调过滤算法
                recommendedAds = recommendAdsByCollaborativeFiltering(user, numRecommendations);
                break;
            case "SVD":
                //基于SVD算法
                recommendedAds = recommendAdsBySVD(user, numRecommendations);
                break;
            case "positionAndExposure":
                //基于位置归一化和曝光频次控制算法
                recommendedAds = recommendAdsByPositionAndExposure(user, numRecommendations);
                break;
            case "AI":
                recommendedAds = recommendAdsByAI(user, numRecommendations);
            default:
                throw new RuntimeException("未知的推荐引擎");
        }

        // 添加多样性并探索性
        return addDiversityAndExploration(recommendedAds, numRecommendations);
    }

    /*===================基于AI实现=======================*/

    private List<Ad> recommendAdsByAI(User user, int numRecommendations) {

        return null;
    }


    /*===================基于内容推荐算法实现=======================*/

    /**
     * 基于内容推荐
     */
    private List<Ad> recommendAdsByContent(User user, int numRecommendations) {
        List<String> userInterests = getUserInterests(user);
        List<Ad> allAds = getAllAds();

        // 为每个广告计算相似度
        Map<Ad, Double> adSimilarityMap = new HashMap<>();
        for (Ad ad : allAds) {
            List<String> adKeywords = getAdKeywords(ad);
            double similarity = calculateSimilarity(userInterests, adKeywords);
            adSimilarityMap.put(ad, similarity);
        }

        // 按相似度排序并获取最相似的广告
        return adSimilarityMap.entrySet().stream()
                .sorted(Map.Entry.<Ad, Double>comparingByValue().reversed())
                .limit(numRecommendations)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 计算相似度
    private double calculateSimilarity(List<String> userInterests, List<String> adKeywords) {
        Set<String> commonKeywords = new HashSet<>(userInterests);
        commonKeywords.retainAll(adKeywords);
        return (double) commonKeywords.size() / (userInterests.size() + adKeywords.size());
    }

    // 获取用户的兴趣列表
    private List<String> getUserInterests(User user) {
        return Arrays.asList(user.getInterest().split(","));
    }

    // 获取广告列表（从缓存/数据库中获取）
    private List<Ad> getAllAds() {
        String jsonStr = RedisUtil.get(RedisConstant.AD);
        List<Ad> list = JSONArray.parseArray(jsonStr, Ad.class);
        if (ValidateUtil.isBlank(list)) {
            List<Ad> list1 = adMapper.selectList(null);
            CompletableFuture.runAsync(() -> {
                JSONArray from = JSONArray.from(list1);
                RedisUtil.set(RedisConstant.AD, from.toJSONString(JSONWriter.Feature.WriteMapNullValue));
            });
            return list1;
        }
        return list;
    }

    // 获取广告的关键词列表
    private List<String> getAdKeywords(Ad ad) {
        return Arrays.asList(ad.getKeyWords().split(","));
    }

    /*===================基于协调过滤算法实现=======================*/

    /**
     * 基于协同过滤推荐
     */
    private List<Ad> recommendAdsByCollaborativeFiltering(User user, int numRecommendations) {
        List<UserBehavior> allUserBehaviors = getAllUserBehaviors();
        Map<String, List<String>> userAdMap = getUserAdMap(allUserBehaviors);

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
                .map(this::getAdById)
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

    // 获取所有用户的行为数据
    private List<UserBehavior> getAllUserBehaviors() {
        // 从缓存或数据库获取用户行为数据
        String jsonStr = RedisUtil.get(RedisConstant.USER_BEHAVIOR);
        List<UserBehavior> list = JSONArray.parseArray(jsonStr, UserBehavior.class);
        if (ValidateUtil.isBlank(list)) {
            List<UserBehavior> list1 = userBehaviorMapper.selectList(null);
            CompletableFuture.runAsync(() -> {
                JSONArray from = JSONArray.from(list1);
                RedisUtil.set(RedisConstant.USER_BEHAVIOR, from.toJSONString(JSONWriter.Feature.WriteMapNullValue));
            });
            return list1;
        }
        return list;
    }

    // 获取用户广告映射
    private Map<String, List<String>> getUserAdMap(List<UserBehavior> allUserBehaviors) {
        Map<String, List<String>> userAdMap = new HashMap<>();
        for (UserBehavior behavior : allUserBehaviors) {
            userAdMap.computeIfAbsent(behavior.getUserId(), k -> new ArrayList<>()).add(behavior.getAdId());
        }
        return userAdMap;
    }

    /*===================基于SVD算法实现=======================*/

    /**
     * 基于SVD算法
     */
    private List<Ad> recommendAdsBySVD(User user, int numRecommendations) {
        List<UserBehavior> allUserBehaviors = getAllUserBehaviors();
        Map<String, List<String>> userAdMap = getUserAdMap(allUserBehaviors);

        List<String> userIds = new ArrayList<>(userAdMap.keySet());
        List<String> adIds = getAllAds().stream()
                .map(Ad::getId)
                .collect(Collectors.toList());

        double[][] data = buildUserAdMatrix(userAdMap, userIds, adIds);
        SVDRecommendation svdRec = new SVDRecommendation(data, userIds, adIds);
        List<String> recommendedAdIds = svdRec.recommend(user.getId(), numRecommendations);

        return recommendedAdIds.stream()
                .map(this::getAdById)
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

    /*===================基于位置归一化和曝光频次控制算法实现=======================*/

    /**
     * 基于位置归一化和曝光频次控制算法实现
     */
    private List<Ad> recommendAdsByPositionAndExposure(User user, int numRecommendations) {
        List<String> userInterests = getUserInterests(user);
        List<Ad> allAds = getAllAds();
        double maxPosition = allAds.stream().mapToDouble((ad)-> Double.parseDouble(ad.getPosition())).max().orElse(1.0);

        // 为每个广告计算综合权重
        Map<Ad, Double> adWeightMap = new HashMap<>();
        for (Ad ad : allAds) {
            List<String> adKeywords = getAdKeywords(ad);
            double similarity = calculateSimilarity(userInterests, adKeywords);
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


    /**
     * 增加推荐结果的多样性和探索机制
     */
    private List<Ad> addDiversityAndExploration(List<Ad> recommendedAds, int numRecommendations) {
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
            Ad ad = getAdById(adId);
            String category = ad.getType();
            categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
            categoryInterestMap.put(entry.getKey(), entry.getValue() / (double) behaviorList.size());
        }

        return categoryInterestMap;
    }
}
