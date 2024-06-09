package com.planb.controller.common;

import com.planb.constant.UserConstants;
import com.planb.entity.User;
import com.planb.service.IUserService;
import com.planb.vo.Result;
import com.planb.dto.user.UserUpdateDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Api(tags = "用户信息模块相关接口")
@RequiredArgsConstructor
public class UserDataController {

    private final IUserService userService;

    @GetMapping("/check")
    @ApiOperation("查看个人资料")
    public Result check() {
        User user = userService.check();
        return Result.ok(user);
    }

    @PutMapping
    @ApiOperation("修改个人资料")
    public Result update(@RequestBody UserUpdateDto user) {
        userService.update(user);
        return Result.ok(UserConstants.UPDATE_PROFILE_SUCCESS);
    }

}
