package com.planb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户表
 */
@Data
@TableName("user")
@ApiModel("用户表")
public class User {
    @TableId
    private String id;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("昵称")
    private String name;
    @ApiModelProperty("姓名")
    private String interest;
    @ApiModelProperty("创建时间")
    private String created;
    @ApiModelProperty("修改时间")
    private String modifyed;
    @ApiModelProperty("用户类型")
    private String type;
}