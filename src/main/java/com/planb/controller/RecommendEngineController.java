package com.planb.controller;

import com.planb.constant.RedisConstant;
import com.planb.dto.engine.ChangeEngineDto;
import com.planb.entity.Dict;
import com.planb.utils.DictUtil;
import com.planb.utils.RedisUtil;
import com.planb.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        log.info(dict.toString());
        log.info(dict.getDictname());
        RedisUtil.set(RedisConstant.RECOMMEND_ENGINE, dict.getDictname());
        return Result.ok();
    }

    @GetMapping("/get")
    @ApiOperation("获取当前推荐引擎")
    public Result getCurrentEngine() {
        String engine = RedisUtil.get(RedisConstant.RECOMMEND_ENGINE);
        return Result.ok(engine);
    }
}