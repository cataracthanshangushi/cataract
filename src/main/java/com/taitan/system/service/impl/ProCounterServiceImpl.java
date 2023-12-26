package com.taitan.system.service.impl;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taitan.system.mapper.ProCollectMapper;
import com.taitan.system.mapper.ProCounterMapper;
import com.taitan.system.pojo.entity.ProCollect;
import com.taitan.system.pojo.entity.ProCounter;
import com.taitan.system.service.ProCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author PB080086
 * @since 2023-09-19
 */
@Service
@RequiredArgsConstructor
public class ProCounterServiceImpl extends ServiceImpl<ProCounterMapper, ProCounter> implements ProCounterService {

    private final ProCounterMapper proCounterMapper;

    @Override
    public Long saveProCounter(ProCounter proCounter) {
        this.save(proCounter);
        return proCounter.getProId();
    }

    @Override
    public Long updateProCounter(ProCounter proCounter) {
        long count = this.count(new LambdaQueryWrapper<ProCounter>()
                .eq(ProCounter::getProId, proCounter.getProId())
        );
        Assert.isTrue(count != 0, "ID不存在");
        QueryWrapper<ProCounter> wrapper = new QueryWrapper<>();
        wrapper.eq("pro_id",proCounter.getProId());
        proCounterMapper.update(proCounter,wrapper);
        //this.updateById(proCounter);
        return proCounter.getProId();
    }
    /**
     * 删除用户
     *
     * @param idsStr 产品用户ID，多个以英文逗号(,)分割
     * @return
     */
    @Override
    public boolean deleteByIds(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "删除的数据为空");
        // 逻辑删除
        List<Long> ids = Arrays.asList(idsStr.split(",")).stream()
                .map(idStr -> Long.parseLong(idsStr)).collect(Collectors.toList());
        boolean result = this.removeByIds(ids);
        return result;

    }

    @Override
    public ProCounter getProCounter(Long id) {
        ProCounter proCounter = this.getOne(new LambdaQueryWrapper<ProCounter>()
                .eq(ProCounter::getProId, id)
                .select()
        );
        return proCounter;
    }

    @Override
    public Long addProCounter(Long proId) {
        ProCounter proCounter = this.getProCounter(proId);
        if(ObjectUtil.isNotEmpty(proCounter)){
            Long proViews = proCounter.getProViews();
            proCounter.setProViews(proViews+1);
            return this.updateProCounter(proCounter);
        }else {
            proCounter = new ProCounter();
            proCounter.setProId(proId);
            proCounter.setProCollection(0L);
            proCounter.setProViews(1L);
            return this.saveProCounter(proCounter);
        }
    }
}
