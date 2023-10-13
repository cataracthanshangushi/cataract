package com.taitan.system.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taitan.system.mapper.ProCollectMapper;
import com.taitan.system.pojo.entity.ProCollect;
import com.taitan.system.pojo.vo.ProductDetailVO;
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
        long count = this.count(new LambdaQueryWrapper<ProCollect>()
                .eq(ProCollect::getProId, proCollect.getProId())
                .eq(ProCollect::getUserId, proCollect.getUserId()));
        Assert.isTrue(count == 0, "收藏失败，已经收藏");
        return this.save(proCollect);
    }

    @Override
    public boolean deleteProCollect(ProCollect proCollect) {
        QueryWrapper<ProCollect> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", proCollect.getUserId());
        wrapper.eq("pro_id", proCollect.getProId());
        return this.remove(wrapper);
    }

    @Override
    public List<ProductDetailVO> getProCollectByUserId(Long id) {
        return this.proCollectMapper.getProDetailByCol(id);
//        QueryWrapper<ProCollect> wrapper = new QueryWrapper<>();
//        wrapper.eq("user_id", id);
//        return this.list(wrapper);
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
