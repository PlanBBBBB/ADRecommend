package com.planb.dto.behavior;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加行为-dto")
public class AddBehaviorDto {
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("广告id")
    private String adId;
    @ApiModelProperty("行为类型")
    private String action;
    @ApiModelProperty("行为时间")
    private String waitTime;
}
