package com.planb.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("当前引擎")
public class CurrentEngineVO {

    @ApiModelProperty("字典名")
    private String dictname;

    @ApiModelProperty("字典编号")
    private String dictcode;
}
