package com.planb.controller.common;

import com.planb.constant.UserConstants;
import com.planb.dto.user.UserLoginDto;
import com.planb.service.IUserService;
import com.planb.vo.Result;
import com.planb.dto.user.UserRegisterDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/common")
@Api(tags = "公共模块相关接口")
@RequiredArgsConstructor
public class CommonController {

    private final IUserService userService;

    @PostMapping("/register")
    @ApiOperation("注册")
    public Result register(@RequestBody UserRegisterDto userRegisterVo) {
        return userService.register(userRegisterVo);
    }

    @PostMapping("/login")
    @ApiOperation("登录")
    public Result login(@RequestBody UserLoginDto userLoginVo) {
        String jwt = userService.login(userLoginVo);
        return Result.ok(jwt);
    }


    @GetMapping("/logout")
    @ApiOperation("登出")
    public Result logout() {
        userService.logout();
        return Result.ok(UserConstants.LOGOUT_SUCCESS);
    }

}
