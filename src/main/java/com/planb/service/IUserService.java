package com.planb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.planb.dto.user.UserLoginDto;
import com.planb.dto.user.UserQueryDto;
import com.planb.entity.User;
import com.planb.vo.Result;
import com.planb.dto.user.UserRegisterDto;
import com.planb.dto.user.UserUpdateDto;

public interface IUserService {
    Result register(UserRegisterDto userRegisterVo);

    String login(UserLoginDto userLoginVo);

    void logout();

    User check();

    void update(UserUpdateDto user);

    IPage<User> pageUser(UserQueryDto dto);
}
