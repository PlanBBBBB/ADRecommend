package com.planb.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.planb.constant.RedisConstant;
import com.planb.dao.DictMapper;
import com.planb.dao.UserBehaviorMapper;
import com.planb.entity.Dict;
import com.planb.entity.UserBehavior;
import com.planb.service.GlobalService;
import com.planb.service.IAdService;
import com.planb.utils.RedisUtil;
import com.planb.vo.CurrentEngineVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        CurrentEngineVo vo = new CurrentEngineVo();
        vo.setDictcode("500000");
        vo.setDictname("content");
        JSONObject jsonObject = JSONObject.from(vo);
        RedisUtil.set(RedisConstant.RECOMMEND_ENGINE, jsonObject.toString());
    }
}
