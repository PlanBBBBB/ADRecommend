package com.planb.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.planb.constant.RedisConstant;
import com.planb.dao.DictMapper;
import com.planb.dao.UserBehaviorMapper;
import com.planb.entity.Dict;
import com.planb.entity.UserBehavior;
import com.planb.service.GlobalService;
import com.planb.service.IAdService;
import com.planb.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GlobalServiceImpl implements GlobalService {

    private final DictMapper dictMapper;
    private final IAdService adService;
    private final UserBehaviorMapper userBehaviorMapper;

    /**
     * 初始化字典到redis
     */
    @Override
    public void initDict() {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getIsValid, 1);
        List<Dict> dictList = dictMapper.selectList(queryWrapper);
        JSONArray from = JSONArray.from(dictList);
        RedisUtil.set(RedisConstant.DICT, from.toJSONString(JSONWriter.Feature.WriteMapNullValue));
    }

    @Override
    public void initAd() {
        adService.storeAdToRedis();
    }

    @Override
    public void initUserBehavior() {
        List<UserBehavior> behaviorList = userBehaviorMapper.selectList(null);
        JSONArray from = JSONArray.from(behaviorList);
        RedisUtil.set(RedisConstant.USER_BEHAVIOR, from.toJSONString(JSONWriter.Feature.WriteMapNullValue));
    }

    @Override
    public void initEngine() {
        Map<String, String> map = new HashMap<>();
        map.put(RedisConstant.DEFAULT_ENGINE_CODE_KEY, RedisConstant.DEFAULT_ENGINE_CODE_VALUE);
        map.put(RedisConstant.DEFAULT_ENGINE_NAME_KEY, RedisConstant.DEFAULT_ENGINE_NAME_VALUE);
        RedisUtil.setHash(RedisConstant.RECOMMEND_ENGINE, map);
    }
}
