package com.taitan.system.service.impl;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taitan.system.mapper.BoothOrderMapper;
import com.taitan.system.pojo.entity.BoothOrder;
import com.taitan.system.service.BoothOrderService;
import com.taitan.system.service.BoothOrderService;
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
public class BoothOrderServiceImpl extends ServiceImpl<BoothOrderMapper, BoothOrder> implements BoothOrderService {

    private final BoothOrderMapper boothOrderMapper;

    @Override
    public Long saveBoothOrder(BoothOrder boothOrder) {
        this.save(boothOrder);
        return boothOrder.getId();
    }

    @Override
    public Long updateBoothOrder(BoothOrder boothOrder) {
        long count = this.count(new LambdaQueryWrapper<BoothOrder>()
                .eq(BoothOrder::getId, boothOrder.getId())
        );
        Assert.isTrue(count != 0, "订单ID不存在");
        this.updateById(boothOrder);
        return boothOrder.getId();
    }
    /**
     * 删除用户
     *
     * @param idsStr 产品用户ID，多个以英文逗号(,)分割
     * @return
     */
    @Override
    public boolean deleteByIds(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "删除的订单数据为空");
        // 逻辑删除
        List<Long> ids = Arrays.asList(idsStr.split(",")).stream()
                .map(idStr -> Long.parseLong(idsStr)).collect(Collectors.toList());
        boolean result = this.removeByIds(ids);
        return result;

    }

    @Override
    public BoothOrder getBoothOrder(Long id) {
        BoothOrder boothOrder = this.getOne(new LambdaQueryWrapper<BoothOrder>()
                .eq(BoothOrder::getId, id)
                .select()
        );
        return boothOrder;
    }

    @Override
    public List<BoothOrder> getBoothOrderByUserId(Long userid) {
        QueryWrapper<BoothOrder> wrapper=new QueryWrapper<>();
        wrapper.eq("user_id",userid);
        List<BoothOrder> userList = boothOrderMapper.selectList(wrapper);
        return userList;
    }


}
