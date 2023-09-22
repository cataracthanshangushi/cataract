package com.taitan.system.service.impl;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taitan.system.mapper.ProContactMapper;
import com.taitan.system.pojo.entity.ProContact;
import com.taitan.system.service.ProContactService;
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
public class ProContactServiceImpl extends ServiceImpl<ProContactMapper, ProContact> implements ProContactService {

    private final ProContactMapper proContactMapper;

    @Override
    public Long saveProContact(ProContact proContact) {
        this.save(proContact);
        return proContact.getId();
    }

    @Override
    public Long updateProContact(ProContact proContact) {
        this.updateById(proContact);
        return proContact.getId();
    }
    /**
     * 删除用户
     *
     * @param idsStr 产品用户ID，多个以英文逗号(,)分割
     * @return
     */
    @Override
    public boolean deleteByIds(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "删除的产品人数据为空");
        // 逻辑删除
        List<Long> ids = Arrays.asList(idsStr.split(",")).stream()
                .map(idStr -> Long.parseLong(idsStr)).collect(Collectors.toList());
        boolean result = this.removeByIds(ids);
        return result;

    }

    @Override
    public ProContact getProContact(Long id) {
        ProContact proContact = this.getOne(new LambdaQueryWrapper<ProContact>()
                .eq(ProContact::getId, id)
                .select()
        );
        return proContact;
    }

    @Override
    public List<ProContact> getProContactByUserId(Long userid) {
        QueryWrapper<ProContact> wrapper=new QueryWrapper<>();
        wrapper.eq("user_id",userid);
        List<ProContact> userList = proContactMapper.selectList(wrapper);
        return userList;
    }


}
