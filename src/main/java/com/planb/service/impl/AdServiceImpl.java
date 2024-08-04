package com.planb.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.planb.constant.AIConstant;
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
import com.planb.util.AiUtil;
import com.planb.util.DictUtil;
import com.planb.util.RecommendUtil;
import com.planb.util.RedisUtil;
import com.planb.util.threadpool.GlobalThreadPool;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdServiceImpl implements IAdService {

    private final AdMapper adMapper;
    private final RedisLimiterManager redisLimiterManager;
    private final ExecutorService executor = GlobalThreadPool.getInstance();
    private final RecommendUtil recommendUtil;
    private final RecommendationStrategyFactory recommendationStrategyFactory;
    private final AiUtil aiUtil;

    @Override
    public Ad getAdById(String id) {
        return adMapper.selectById(id);
    }

    @Override
    public void add(AddAdDto dto) {
        Ad ad = new Ad();
        BeanUtils.copyProperties(dto, ad);
        adMapper.insert(ad);
        this.storeAdToRedis();
    }

    @Override
    public void update(UpAdDto dto) {
        Ad ad = adMapper.selectById(dto.getId());
        BeanUtils.copyProperties(dto, ad);
        adMapper.updateById(ad);
        this.storeAdToRedis();
    }

    @Override
    public void delete(String id) {
        Ad ad = adMapper.selectById(id);
        ad.setIsValid("0");
        adMapper.updateById(ad);
        this.storeAdToRedis();
    }

    @Override
    public void storeAdToRedis() {
        CompletableFuture.runAsync(() -> {
            RedisUtil.delete(RedisConstant.AD);
            LambdaQueryWrapper<Ad> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Ad::getIsValid, 1);
            List<Ad> adList = adMapper.selectList(queryWrapper);
            HashMap<String, String> hashMap = new HashMap<>();
            adList.forEach(ad -> {
                String key = ad.getId();
                hashMap.put(key, JSONUtil.toJsonStr(ad));
            });
            RedisUtil.setHash(RedisConstant.AD, hashMap);
        }, executor);
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


    @Override
    public List<Ad> recommendByAI(String num) {
        // 获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userId = loginUser.getUser().getId();

        // 限流操作
        redisLimiterManager.doRateLimit(LimiterConstant.GET_RECOMMEND_KEY + userId);

        // 构建AI请求消息
        String userMessage = "请对编号为" + userId + "的用户推荐" + num + "个广告";
        String result = aiUtil.doSyncStableRequest(AIConstant.SYSTEM_MESSAGE, userMessage);
        String[] split = result.split(",");
        if (split.length != Integer.parseInt(num)) {
            throw new RuntimeException(AIConstant.ERROR_MESSAGE);
        }

        // 获取广告信息
        Map<String, String> map = RedisUtil.getHash(RedisConstant.AD);

        List<Ad> adList = new ArrayList<>();
        for (String s : split) {
            String adJson = map.get(s);
            Ad ad = JSONUtil.toBean(adJson, Ad.class);
            adList.add(ad);
        }
        return adList;
    }

    @Override
    public SseEmitter recommendByAISSe(String num) {
        // 获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userId = loginUser.getUser().getId();

        // 限流操作
        redisLimiterManager.doRateLimit(LimiterConstant.GET_RECOMMEND_KEY + userId);

        // 获取广告信息
        Map<String, String> map = RedisUtil.getHash(RedisConstant.AD);

        // 构建AI请求消息
        String userMessage = "请对编号为" + userId + "的用户推荐" + num + "个广告";

        // 建立 SSE 连接对象，0 表示永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        // AI 生成，SSE 流式返回
        Flowable<ModelData> modelDataFlowable = aiUtil.doStreamRequest(AIConstant.SYSTEM_MESSAGE, userMessage, null);
        // 拼接完整广告id
        StringBuilder stringBuilder = new StringBuilder();

        modelDataFlowable
                .observeOn(Schedulers.io())
                .map(modelData -> modelData.getChoices().get(0).getDelta().getContent())
                .flatMap(message -> {
                    List<Character> characterList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        characterList.add(c);
                    }
                    return Flowable.fromIterable(characterList);
                })
                .doOnNext(c -> {
                    if (c == ',') {
                        String adJson = map.get(stringBuilder.toString());
                        // 通过 SSE 返回给前端
                        sseEmitter.send(adJson);
                        // 重置，准备拼接下一个广告id
                        stringBuilder.setLength(0);
                    } else {
                        stringBuilder.append(c);
                    }
                })
                .doOnError((e) -> log.error("sse error", e))
                .doOnComplete(() -> {
                    // 在完成时检查是否还有未处理的字符串
                    if (stringBuilder.length() > 0) {
                        String adJson = map.get(stringBuilder.toString());
                        sseEmitter.send(adJson);
                        stringBuilder.setLength(0);
                    }
                    sseEmitter.complete();
                })
                .subscribe();
        return sseEmitter;
    }

}
