package com.planb.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserUpdateDto {
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("昵称")
    private String name;
    @ApiModelProperty("兴趣标签，多个用逗号分隔")
    private String interest;
}
