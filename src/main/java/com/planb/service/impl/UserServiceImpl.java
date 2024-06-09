package com.planb.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.planb.constant.UserConstants;
import com.planb.dao.UserMapper;
import com.planb.dto.user.UserLoginDto;
import com.planb.security.LoginUser;
import com.planb.entity.User;
import com.planb.service.IUserService;
import com.planb.utils.JwtUtil;
import com.planb.utils.RedisUtil;
import com.planb.vo.Result;
import com.planb.dto.user.UserRegisterDto;
import com.planb.dto.user.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService, UserDetailsService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public Result register(UserRegisterDto userRegisterVo) {
        if (userRegisterVo == null || userRegisterVo.getUsername() == null || userRegisterVo.getPassword() == null) {
            return Result.fail(UserConstants.USERNAME_OR_PASSWORD_EMPTY);
        }
        //判断用户名是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName, userRegisterVo.getUsername());
        User u = userMapper.selectOne(wrapper);
        if (u != null) {
            return Result.fail(UserConstants.USER_ALREADY_EXISTS);
        }
        User user = new User();
        String password = userRegisterVo.getPassword();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        password = passwordEncoder.encode(password);
        BeanUtils.copyProperties(userRegisterVo, user);
        user.setPassword(password);
        user.setUserName(userRegisterVo.getUsername());
        //注册
        userMapper.insert(user);
        return Result.ok(UserConstants.REGISTRATION_SUCCESS);
    }


    @Override
    public String login(UserLoginDto userLoginVo) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginVo.getUsername(), userLoginVo.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException(UserConstants.INVALID_USERNAME_OR_PASSWORD);
        }
        //使用userid生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        User user = loginUser.getUser();
        String userId = user.getId();
        String token = JwtUtil.createJWT(userId);

        String loginUserJson = JSONUtil.toJsonStr(loginUser);
        //authenticate存入redis
        String key = UserConstants.LOGIN_PREFIX + userId;
        RedisUtil.setEx(key, loginUserJson, UserConstants.TIME_OUT, TimeUnit.MINUTES);
        //把token响应给前端
        return token;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException(UserConstants.USER_NOT_FOUND);
        }
        // 根据用户查询权限信息 添加到LoginUser中
        String role = user.getType();
        List<String> list = new ArrayList<>();
        list.add(role);
        return new LoginUser(user, list);
    }


    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userId = loginUser.getUser().getId();
        RedisUtil.delete(UserConstants.LOGIN_PREFIX + userId);
    }

    @Override
    public User check() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser.getUser();
    }

    @Override
    public void update(UserUpdateDto userUpdateVo) {
        String key = UserConstants.LOGIN_PREFIX + getUserId();
        String loginUserJson = RedisUtil.get(key);
        LoginUser loginUser = JSONUtil.toBean(loginUserJson, LoginUser.class);
        User loginUserUser = loginUser.getUser();
        User user = userMapper.selectById(getUserId());
        if (userUpdateVo.getPassword() != null && !userUpdateVo.getPassword().isEmpty()) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String password = bCryptPasswordEncoder.encode(userUpdateVo.getPassword());
            user.setPassword(password);
            loginUserUser.setPassword(password);
        }
        if (userUpdateVo.getName() != null && !userUpdateVo.getName().isEmpty()) {
            user.setName(userUpdateVo.getName());
            loginUserUser.setName(userUpdateVo.getName());
        }
        userMapper.updateById(user);
        loginUser.setUser(loginUserUser);
        String jsonStr = JSONUtil.toJsonStr(loginUser);
        RedisUtil.set(key, jsonStr);
    }

    private String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser.getUser().getId();
    }
}
