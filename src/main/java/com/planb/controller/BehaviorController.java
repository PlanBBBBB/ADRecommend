package com.planb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.planb.dto.behavior.AddBehaviorDto;
import com.planb.dto.behavior.PageBehaviorDto;
import com.planb.entity.UserBehavior;
import com.planb.service.IUserBehaviorService;
import com.planb.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/behavior")
@Api(tags = "用户行为相关接口")
@RequiredArgsConstructor
public class BehaviorController {

    private final IUserBehaviorService userBehaviorService;

    @PostMapping("add")
    @ApiOperation("新增用户行为记录")
    public Result add(@RequestBody AddBehaviorDto dto){
        userBehaviorService.add(dto);
        return Result.ok();
    }


    @PostMapping("pageBehavior")
    @ApiOperation("后台分页获取用户行为")
    public Result pageBehavior(@RequestBody PageBehaviorDto dto){
        int currentPage = dto.getCurrentPage();
        IPage<UserBehavior> page = userBehaviorService.pageBehavior(dto);
        //如果当前页码值大于总页码值，那么重新执行查询操作，使用最大页码值作为当前页码值
        if (currentPage > page.getPages()) {
            dto.setCurrentPage((int) page.getPages());
            page = userBehaviorService.pageBehavior(dto);
        }
        return Result.ok(page);
    }

}
