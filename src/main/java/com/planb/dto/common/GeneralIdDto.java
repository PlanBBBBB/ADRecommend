package com.planb.dto.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("通用<id>请求Dto")
public class GeneralIdDto {

    @ApiModelProperty(value = "通用主键id")
    private String id;

}
