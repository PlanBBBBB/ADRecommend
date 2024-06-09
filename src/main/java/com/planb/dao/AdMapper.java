package com.planb.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.planb.entity.Ad;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdMapper extends BaseMapper<Ad> {
}
