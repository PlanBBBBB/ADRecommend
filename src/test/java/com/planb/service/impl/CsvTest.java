package com.planb.service.impl;

import com.planb.dao.AdMapper;
import com.planb.dao.UserBehaviorMapper;
import com.planb.dao.UserMapper;
import com.planb.entity.Ad;
import com.planb.entity.User;
import com.planb.entity.UserBehavior;
import com.planb.util.CsvUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class CsvTest {

    @Resource
    private UserBehaviorMapper userBehaviorMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private AdMapper admapper;

    @Test
    void testCsv() {
        List<UserBehavior> behaviorList = userBehaviorMapper.selectList(null);
        String behaviorsCsv = CsvUtil.behaviorsToCsv(behaviorList);
        System.out.println(behaviorsCsv);
        System.out.println("----------------");
        List<User> users = userMapper.selectList(null);
        String usersCsv = CsvUtil.usersToCsv(users);
        System.out.println(usersCsv);
        System.out.println("----------------");
        List<Ad> ads = admapper.selectList(null);
        String adsCsv = CsvUtil.adToCsv(ads);
        System.out.println(adsCsv);
    }
}
