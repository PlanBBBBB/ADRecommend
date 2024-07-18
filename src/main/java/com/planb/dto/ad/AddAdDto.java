package com.planb.dto.ad;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加广告-dto")
public class AddAdDto {

    @ApiModelProperty("关键词，字典300，多选用逗号隔开")
    private String keyWords;
    @ApiModelProperty("广告图片url")
    private String imgUrl;
    @ApiModelProperty("广告跳转链接url")
    private String targetUrl;
    @ApiModelProperty("广告投放开始时间")
    private String startTime;
    @ApiModelProperty("广告投放结束时间")
    private String endTime;
    @ApiModelProperty("广告类型，字典200")
    private String type;
    @ApiModelProperty("位置值（从0到10，数字越小越靠上）")
    private String position;
}
