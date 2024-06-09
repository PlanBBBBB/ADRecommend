package com.planb.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.planb.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
