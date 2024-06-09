package com.planb.dto.ad;

import com.planb.dto.ad.AddAdDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("更新广告-dto")
public class UpAdDto extends AddAdDto {
    @ApiModelProperty("广告id")
    private String id;
}
