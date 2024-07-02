package com.planb.controller.common;

import com.planb.constant.UserConstants;
import com.planb.dto.common.GeneralIdDto;
import com.planb.dto.user.UserLoginDto;
import com.planb.entity.Dict;
import com.planb.service.IUserService;
import com.planb.utils.DictUtil;
import com.planb.vo.DictWithParentVo;
import com.planb.vo.Result;
import com.planb.dto.user.UserRegisterDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


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

    @PostMapping("/getDictByDictType")
    @ApiOperation("根据dictType查询字典")
    public Result getDictByDictType(@RequestBody GeneralIdDto dto) {
        return Result.ok(DictUtil.getDictByType(dto.getId()));
    }

    @PostMapping("/getDictWithParent")
    @ApiOperation("根据dictType查询字典包含子字典")
    public Result getDictWithParent(@RequestBody GeneralIdDto dto) {
        List<Dict> list = DictUtil.getDictByType(dto.getId());
        List<DictWithParentVo> vos = list.stream().map(e -> {
            DictWithParentVo vo = new DictWithParentVo();
            vo.setDictcode(e.getDictcode());
            vo.setDictname(e.getDictname());
            List<Dict> dictByParent = DictUtil.getDictByParent(e.getDictcode());
            List<DictWithParentVo> collect = dictByParent.stream().map(dict -> {
                DictWithParentVo vo1 = new DictWithParentVo();
                vo1.setDictcode(dict.getDictcode());
                vo1.setDictname(dict.getDictname());
                return vo1;
            }).collect(Collectors.toList());
            vo.setChildren(collect);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(vos);
    }

}
