package com.planb.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户行为表
 */
@Data
@TableName("user_behavior")
public class UserBehavior {
    @TableId
    private String id;
    private String userId;
    private String adId;
    private String action;//字典：100
    private String waitTime;
    private String created;
}
