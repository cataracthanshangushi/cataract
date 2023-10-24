package com.taitan.system.service.impl;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taitan.system.mapper.ConApproveMapper;
import com.taitan.system.pojo.entity.ConApprove;
import com.taitan.system.service.ConApproveService;
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
public class ConApproveServiceImpl extends ServiceImpl<ConApproveMapper, ConApprove> implements ConApproveService {

    private final ConApproveMapper conApproveMapper;

    @Override
    public Long saveConApprove(ConApprove conApprove) {
        this.save(conApprove);
        return conApprove.getId();
    }

    @Override
    public Long updateConApprove(ConApprove conApprove) {
        long count = this.count(new LambdaQueryWrapper<ConApprove>()
                .eq(ConApprove::getId, conApprove.getId())
        );
        Assert.isTrue(count != 0, "审核信息ID不存在");
        this.updateById(conApprove);
        return conApprove.getId();
    }
    /**
     * 删除
     *
     * @param idsStr 产品用户ID，多个以英文逗号(,)分割
     * @return
     */
    @Override
    public boolean deleteByIds(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "删除的审核信息数据为空");
        // 逻辑删除
        List<Long> ids = Arrays.asList(idsStr.split(",")).stream()
                .map(idStr -> Long.parseLong(idsStr)).collect(Collectors.toList());
        boolean result = this.removeByIds(ids);
        return result;

    }

    @Override
    public ConApprove getConApprove(Long id) {
        ConApprove conApprove = this.getOne(new LambdaQueryWrapper<ConApprove>()
                .eq(ConApprove::getId, id)
                .select()
        );
        return conApprove;
    }

    @Override
    public List<ConApprove> getConApproveByUserId(Long userid) {
        QueryWrapper<ConApprove> wrapper=new QueryWrapper<>();
        wrapper.eq("user_id",userid);
        List<ConApprove> userList = conApproveMapper.selectList(wrapper);
        return userList;
    }


}
