package com.planb.controller.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.planb.constant.UserConstants;
import com.planb.dto.user.UserQueryDto;
import com.planb.entity.User;
import com.planb.service.IUserService;
import com.planb.vo.Result;
import com.planb.dto.user.UserUpdateDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @ApiOperation("分页获取用户信息")
    @PostMapping("/pageUser")
    @PreAuthorize("hasAuthority('400000')")
    public Result pageUser(@RequestBody UserQueryDto dto) {
        int currentPage = dto.getCurrentPage();
        IPage<User> page = userService.pageUser(dto);
        //如果当前页码值大于总页码值，那么重新执行查询操作，使用最大页码值作为当前页码值
        if (currentPage > page.getPages()) {
            dto.setCurrentPage((int) page.getPages());
            page = userService.pageUser(dto);
        }
        return Result.ok(page);
    }

}
