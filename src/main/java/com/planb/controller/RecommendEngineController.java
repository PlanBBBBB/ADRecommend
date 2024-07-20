package com.planb.controller;

import com.planb.constant.RedisConstant;
import com.planb.dto.engine.ChangeEngineDto;
import com.planb.entity.Dict;
import com.planb.util.DictUtil;
import com.planb.util.RedisUtil;
import com.planb.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/engine")
@Api(tags = "推荐引擎相关接口")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('400000')")
@Slf4j
public class RecommendEngineController {


    @PostMapping("/change")
    @ApiOperation("修改当前推荐引擎")
    public Result changeEngine(@RequestBody ChangeEngineDto dto) {
        Dict dict = DictUtil.getDictByCode(dto.getDictcode());
        RedisUtil.updateHashValue(RedisConstant.RECOMMEND_ENGINE, RedisConstant.DEFAULT_ENGINE_CODE_KEY, dict.getDictcode());
        RedisUtil.updateHashValue(RedisConstant.RECOMMEND_ENGINE, RedisConstant.DEFAULT_ENGINE_NAME_KEY, dict.getDictname());
        return Result.ok();
    }

    @GetMapping("/get")
    @ApiOperation("获取当前推荐引擎")
    public Result getCurrentEngine() {
        Map<String, String> map = RedisUtil.getHash(RedisConstant.RECOMMEND_ENGINE);
        return Result.ok(map);
    }
}