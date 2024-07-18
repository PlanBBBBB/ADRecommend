package com.planb.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ad")
public class Ad {
    @TableId
    private String id;
    @TableField("key_words")
    private String keyWords;//关键词（字典：300）
    private String imgUrl;//图片url
    private String targetUrl;//跳转url
    private String startTime;//广告投放开始时间
    private String endTime;//广告投放结束时间
    private String created;//创建时间
    private String modified;//修改时间
    private String isValid;//是否有效
    private String position; //位置值（从0到10，数字越小越靠上）
    private int exposureCount;// 曝光次数
    private String type;//广告类型（字典：200）
}
