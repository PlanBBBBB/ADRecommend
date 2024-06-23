package com.planb.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("dict")
public class Dict {
    @TableId
    @ApiModelProperty("字典编号")
    private String dictcode;
    @ApiModelProperty("字典名称")
    private String dictname;
    @ApiModelProperty("字典类型")
    private String dicttype;
    @ApiModelProperty("字典描述")
    private String dictdesc;
    @ApiModelProperty("是否有效")
    @TableField("isValid")
    private String isValid;
    @ApiModelProperty("字典排序")
    private String dictsort;
    @ApiModelProperty("创建时间")
    private String created;
    @ApiModelProperty("父字典")
    private String parentcode;
    @ApiModelProperty("备注")
    private String remark;
}
