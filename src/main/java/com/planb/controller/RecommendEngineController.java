package com.planb.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.planb.constant.RedisConstant;
import com.planb.dto.engine.ChangeEngineDto;
import com.planb.entity.Dict;
import com.planb.util.DictUtil;
import com.planb.util.RedisUtil;
import com.planb.vo.CurrentEngineVO;
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
        CurrentEngineVO vo = new CurrentEngineVO();
        vo.setDictcode(dict.getDictcode());
        vo.setDictname(dict.getDictname());
        JSONObject jsonObject = JSONObject.from(vo);
        // 设置当前推荐引擎
        RedisUtil.set(RedisConstant.RECOMMEND_ENGINE, jsonObject.toString());
        return Result.ok();
    }

    @GetMapping("/get")
    @ApiOperation("获取当前推荐引擎")
    public Result getCurrentEngine() {
        String json = RedisUtil.get(RedisConstant.RECOMMEND_ENGINE);
        CurrentEngineVO vo = JSON.parseObject(json, CurrentEngineVO.class);
        return Result.ok(vo);
    }
}