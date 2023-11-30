package com.taitan.system.service.impl;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taitan.system.mapper.UserFeedbackMapper;
import com.taitan.system.pojo.entity.UserFeedback;
import com.taitan.system.service.UserFeedbackService;
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
public class UserFeedbackServiceImpl extends ServiceImpl<UserFeedbackMapper, UserFeedback> implements UserFeedbackService {

    private final UserFeedbackMapper UserFeedbackMapper;

    @Override
    public Long saveUserFeedback(UserFeedback userFeedback) {
        this.save(userFeedback);
        return userFeedback.getId();
    }

    @Override
    public Long updateUserFeedback(UserFeedback userFeedback) {
        long count = this.count(new LambdaQueryWrapper<UserFeedback>()
                .eq(UserFeedback::getId, userFeedback.getId())
        );
        Assert.isTrue(count != 0, "ID不存在");
        this.updateById(userFeedback);
        return userFeedback.getId();
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
    public UserFeedback getUserFeedback(Long id) {
        UserFeedback userFeedback = this.getOne(new LambdaQueryWrapper<UserFeedback>()
                .eq(UserFeedback::getId, id)
                .select()
        );
        return userFeedback;
    }

    @Override
    public List<UserFeedback> getUserFeedbackByUserId(Long userid) {
        QueryWrapper<UserFeedback> wrapper=new QueryWrapper<>();
        wrapper.eq("user_id",userid);
        List<UserFeedback> userList = UserFeedbackMapper.selectList(wrapper);
        return userList;
    }


}
