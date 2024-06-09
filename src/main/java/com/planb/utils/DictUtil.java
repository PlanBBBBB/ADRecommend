package com.planb.utils;

import com.alibaba.fastjson2.JSONArray;
import com.planb.constant.RedisConstant;
import com.planb.entity.Dict;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公共获取字典
 */
public class DictUtil {

    /**
     * 获取字典列表
     */
    public static List<Dict> getDictList() {
        String jsonStr = RedisUtil.get(RedisConstant.DICT);
        List<Dict> list = JSONArray.parseArray(jsonStr, Dict.class);
        if (ValidateUtil.isBlank(list)) {
            return Collections.emptyList();
        }
        return list;
    }

    /**
     * 根据type获取字典列表
     */
    public static List<Dict> getDictByType(String type) {
        List<Dict> list = getDictList();
        if (ValidateUtil.isNotBlank(type)) {
            return list.stream().filter(e -> type.equals(e.getDicttype())).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 根据code获取字典
     */
    public static Dict getDictByCode(String code) {
        List<Dict> list = getDictList();
        if (ValidateUtil.isNotBlank(code)) {
            List<Dict> codeList = list.stream().filter(e -> code.equals(e.getDictcode())).collect(Collectors.toList());
            if (ValidateUtil.isBlank(codeList)) {
                return new Dict();
            } else {
                return codeList.get(0);
            }
        }
        return new Dict();
    }

    /**
     * 根据parent获取字典列表
     */
    public static List<Dict> getDictByParent(String parent) {
        List<Dict> list = getDictList();
        if (ValidateUtil.isNotBlank(parent)) {
            return list.stream().filter(e -> parent.equals(e.getParentcode())).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 根据类型获取字典Map(code与name)
     */
    public static Map<String, String> getDictMapByType(String... types) {
        List<String> filterTypes = Arrays.asList(types);// 要过滤的type列表
        List<Dict> list = getDictList();
        return list.stream().filter(e -> filterTypes.contains(e.getDicttype()))
                .collect(Collectors.toMap(Dict::getDictcode, Dict::getDictname));
    }

}
