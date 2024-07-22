package com.planb.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.planb.constant.LimiterConstant;
import com.planb.constant.RedisConstant;
import com.planb.dao.AdMapper;
import com.planb.dto.ad.AddAdDto;
import com.planb.dto.ad.PageAdDto;
import com.planb.dto.ad.UpAdDto;
import com.planb.entity.Ad;
import com.planb.entity.User;
import com.planb.recommend.stratege.RecommendationStrategy;
import com.planb.recommend.factory.RecommendationStrategyFactory;
import com.planb.security.LoginUser;
import com.planb.service.IAdService;
import com.planb.util.DictUtil;
import com.planb.util.RecommendUtil;
import com.planb.util.RedisUtil;
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

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements IAdService {

    private final AdMapper adMapper;
    private final RedisLimiterManager redisLimiterManager;
    private final ExecutorService executor = GlobalThreadPool.getInstance();
    private final RecommendUtil recommendUtil;
    private final RecommendationStrategyFactory recommendationStrategyFactory;

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

        // 限流操作
        redisLimiterManager.doRateLimit(LimiterConstant.GET_RECOMMEND_KEY + user.getId());

        Map<String, String> map = RedisUtil.getHash(RedisConstant.RECOMMEND_ENGINE);
        String engine = map.get(RedisConstant.DEFAULT_ENGINE_NAME_KEY);

        // 工厂模式+策略模式
        RecommendationStrategy strategy = recommendationStrategyFactory.createStrategy(engine);
        List<Ad> recommendedAds = strategy.recommend(user, numRecommendations);

        // 添加多样性并探索性
        return recommendUtil.addDiversityAndExploration(recommendedAds, numRecommendations);
    }

}
