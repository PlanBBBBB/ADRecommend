package com.planb.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户注册-dto")
public class UserRegisterDto {
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("昵称")
    private String name;
    @ApiModelProperty("兴趣标签，用逗号分隔")
    private String interest;
}
