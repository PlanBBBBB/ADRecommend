package com.planb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.planb.dto.ad.AddAdDto;
import com.planb.dto.ad.PageAdDto;
import com.planb.dto.ad.UpAdDto;
import com.planb.dto.common.GeneralIdDto;
import com.planb.entity.Ad;
import com.planb.service.IAdService;
import com.planb.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ad")
@Api(tags = "广告模块相关接口")
@RequiredArgsConstructor
public class AdController {

    private final IAdService adService;

    @PostMapping("/getAdById")
    @PreAuthorize("hasAuthority('400000')")
    @ApiOperation("根据id获取广告")
    public Result getAdById(@RequestBody GeneralIdDto dto) {
        Ad ad = adService.getAdById(dto.getId());
        return Result.ok(ad);
    }


    @PostMapping("/add")
    @ApiOperation("新增广告")
    @PreAuthorize("hasAuthority('400000')")
    public Result add(@RequestBody AddAdDto dto) {
        adService.add(dto);
        return Result.ok();
    }


    @PostMapping("/update")
    @ApiOperation("修改广告信息")
    @PreAuthorize("hasAuthority('400000')")
    public Result update(@RequestBody UpAdDto dto) {
        adService.update(dto);
        return Result.ok();
    }

    @PostMapping("/delete")
    @ApiOperation("删除广告")
    @PreAuthorize("hasAuthority('400000')")
    public Result delete(@RequestBody GeneralIdDto dto) {
        adService.delete(dto.getId());
        return Result.ok();
    }

    @PostMapping("pageAd")
    @ApiOperation("后台分页获取广告")
    @PreAuthorize("hasAuthority('400000')")
    public Result pageAd(@RequestBody PageAdDto dto) {
        int currentPage = dto.getCurrentPage();
        IPage<Ad> page = adService.pageAd(dto);
        //如果当前页码值大于总页码值，那么重新执行查询操作，使用最大页码值作为当前页码值
        if (currentPage > page.getPages()) {
            dto.setCurrentPage((int) page.getPages());
            page = adService.pageAd(dto);
        }
        return Result.ok(page);
    }

    @GetMapping("recommend")
    @ApiOperation("推荐广告")
    public Result recommend(@RequestBody GeneralIdDto dto) {
        List<Ad> list = adService.recommend(Integer.parseInt(dto.getId()));
        return Result.ok(list);
    }
}
