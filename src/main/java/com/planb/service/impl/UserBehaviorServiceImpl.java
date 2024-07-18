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
import com.planb.util.DictUtil;
import com.planb.util.RedisUtil;
import com.planb.util.threadpool.GlobalThreadPool;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class UserBehaviorServiceImpl implements IUserBehaviorService {

    private final UserBehaviorMapper userBehaviorMapper;
    private final ExecutorService executor = GlobalThreadPool.getInstance();

    @Override
    public void add(AddBehaviorDto dto) {
        CompletableFuture.runAsync(() -> {
            UserBehavior userBehavior = new UserBehavior();
            BeanUtils.copyProperties(dto, userBehavior);
            userBehaviorMapper.insert(userBehavior);
            List<UserBehavior> behaviorList = userBehaviorMapper.selectList(null);
            JSONArray from = JSONArray.from(behaviorList);
            RedisUtil.set(RedisConstant.USER_BEHAVIOR, from.toJSONString(JSONWriter.Feature.WriteMapNullValue));
        }, executor);
    }

    @Override
    public IPage<UserBehavior> pageBehavior(PageBehaviorDto dto) {
        LambdaQueryWrapper<UserBehavior> lqw = new LambdaQueryWrapper<>();
        lqw.like(Strings.isNotEmpty(dto.getAction()), UserBehavior::getAction, dto.getAction());
        IPage<UserBehavior> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        userBehaviorMapper.selectPage(page, lqw);
        List<UserBehavior> records = page.getRecords();
        records.forEach(behavior -> behavior.setAction(DictUtil.changeDict(behavior.getAction())));
        return page;
    }
}
