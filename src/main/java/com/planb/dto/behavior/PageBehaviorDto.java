package com.planb.dto.behavior;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户行为分页-dto")
public class PageBehaviorDto {
    @ApiModelProperty("行为类型")
    private String action;
    @ApiModelProperty("当前页")
    private int currentPage;
    @ApiModelProperty("每页条数")
    private int pageSize;
}
