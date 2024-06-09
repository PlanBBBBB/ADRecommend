package com.planb.dto.engine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("修改引擎-dto")
public class ChangeEngineDto {
    @ApiModelProperty(value = "引擎名称")
    private String dictcode;
}
