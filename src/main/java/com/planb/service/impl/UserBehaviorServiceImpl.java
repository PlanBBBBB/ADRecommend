package com.planb.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.planb.constant.RedisConstant;
import com.planb.dao.UserBehaviorMapper;
import com.planb.dto.behavior.AddBehaviorDto;
import com.planb.dto.behavior.PageBehaviorDto;
import com.planb.entity.UserBehavior;
import com.planb.service.IUserBehaviorService;
import com.planb.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBehaviorServiceImpl implements IUserBehaviorService {

    private final UserBehaviorMapper userBehaviorMapper;

    @Override
    public void add(AddBehaviorDto dto) {
        UserBehavior userBehavior = new UserBehavior();
        BeanUtils.copyProperties(dto, userBehavior);
        userBehaviorMapper.insert(userBehavior);
        List<UserBehavior> behaviorList = userBehaviorMapper.selectList(null);
        JSONArray from = JSONArray.from(behaviorList);
        RedisUtil.set(RedisConstant.USER_BEHAVIOR, from.toJSONString(JSONWriter.Feature.WriteMapNullValue));
    }

    @Override
    public IPage<UserBehavior> pageBehavior(PageBehaviorDto dto) {
        LambdaQueryWrapper<UserBehavior> lqw = new LambdaQueryWrapper<>();
        lqw.like(Strings.isNotEmpty(dto.getAction()), UserBehavior::getAction, dto.getAction());
        IPage<UserBehavior> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        userBehaviorMapper.selectPage(page, lqw);
        return page;
    }
}
