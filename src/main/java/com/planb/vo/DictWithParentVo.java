package com.planb.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("字典包含子字典-vo")
public class DictWithParentVo {
    @ApiModelProperty("字典编号")
    private String dictcode;
    @ApiModelProperty("字典名称")
    private String dictname;
    @ApiModelProperty("子级字典编号")
    List<DictWithParentVo> children;
}
