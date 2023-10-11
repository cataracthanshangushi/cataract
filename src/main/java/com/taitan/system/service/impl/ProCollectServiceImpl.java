package com.taitan.system.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taitan.system.mapper.ProCollectMapper;
import com.taitan.system.pojo.entity.ProCollect;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.service.ProCollectService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProCollectServiceImpl extends ServiceImpl<ProCollectMapper, ProCollect> implements ProCollectService {

    private final ProCollectMapper proCollectMapper;

    @Override
    public boolean saveProCollect(ProCollect proCollect) {
        Assert.isTrue(ObjectUtil.isNotEmpty(proCollect), "收藏失败，数据为空");
        return this.save(proCollect);
    }

    @Override
    public boolean deleteProCollect(ProCollect proCollect) {
        Assert.isTrue(ObjectUtil.isNotEmpty(proCollect), "取消收藏失败，数据为空");
        QueryWrapper<ProCollect> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", proCollect.getUserId());
        wrapper.eq("pro_id", proCollect.getProId());
        return this.remove(wrapper);
    }

    @Override
    public List<ProCollect> getProCollectByUserId(Long id) {
        QueryWrapper<ProCollect> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", id);
        return this.list(wrapper);
    }

    @Override
    public boolean checkProCollect(ProCollect proCollect) {
        QueryWrapper<ProCollect> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", proCollect.getUserId());
        wrapper.eq("pro_id", proCollect.getProId());
        ProCollect result = this.getOne(wrapper);
        return ObjectUtil.isNotEmpty(result) ? true : false;
    }
}
