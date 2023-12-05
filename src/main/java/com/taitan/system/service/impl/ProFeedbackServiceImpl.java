package com.taitan.system.service.impl;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taitan.system.mapper.ProFeedbackMapper;
import com.taitan.system.pojo.entity.ProFeedback;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.service.ProFeedbackService;
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
public class ProFeedbackServiceImpl extends ServiceImpl<ProFeedbackMapper, ProFeedback> implements ProFeedbackService {

    @Override
    public Long saveProFeedback(ProFeedback proFeedback) {
        this.save(proFeedback);
        return proFeedback.getId();
    }

    @Override
    public Long updateProFeedback(ProFeedback proFeedback) {
        long count = this.count(new LambdaQueryWrapper<ProFeedback>()
                .eq(ProFeedback::getId, proFeedback.getId())
        );
        Assert.isTrue(count != 0, "ID不存在");
        this.updateById(proFeedback);
        return proFeedback.getId();
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
    public ProFeedback getProFeedback(Long id) {
        ProFeedback proFeedback = this.getOne(new LambdaQueryWrapper<ProFeedback>()
                .eq(ProFeedback::getId, id)
                .select()
        );
        return proFeedback;
    }

    @Override
    public IPage<ProFeedback> getProFeedbackList(Long userId,Long contactId,Long proId,String proName,Integer pageNum,Integer pageSize) {
        IPage<ProFeedback> page = new Page(pageNum, pageSize);
        IPage<ProFeedback> resultList =this.page(page,new QueryWrapper<ProFeedback>().lambda()
                .like(StrUtil.isNotBlank(proName),ProFeedback::getProName, proName)
                .eq(ObjectUtil.isNotEmpty(userId),ProFeedback::getUserId,userId)
                .eq(ObjectUtil.isNotEmpty(contactId),ProFeedback::getContactId,contactId)
                .eq(ObjectUtil.isNotEmpty(proId),ProFeedback::getProId,proId)
        );

        return resultList;
    }


}
