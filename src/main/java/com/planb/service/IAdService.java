package com.planb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.planb.dto.ad.AddAdDto;
import com.planb.dto.ad.PageAdDto;
import com.planb.dto.ad.UpAdDto;
import com.planb.entity.Ad;

import java.util.List;

public interface IAdService {

    Ad getAdById(String id);

    void add(AddAdDto dto);

    void update(UpAdDto dto);

    void delete(String id);

    IPage<Ad> pageAd(PageAdDto dto);

    List<Ad> recommend(int numRecommendations);

    void storeAdToRedis();

    List<Ad> recommendByAI(String id);
}
