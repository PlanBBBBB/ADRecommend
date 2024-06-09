package com.planb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.planb.dto.behavior.AddBehaviorDto;
import com.planb.dto.behavior.PageBehaviorDto;
import com.planb.entity.UserBehavior;

public interface IUserBehaviorService {
    void add(AddBehaviorDto dto);

    IPage<UserBehavior> pageBehavior(PageBehaviorDto dto);
}
