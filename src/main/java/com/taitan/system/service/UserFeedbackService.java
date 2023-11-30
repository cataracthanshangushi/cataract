package com.taitan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.taitan.system.pojo.entity.UserFeedback;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author PB080086
 * @since 2023-09-19
 */
public interface UserFeedbackService extends IService<UserFeedback> {

    Long saveUserFeedback(UserFeedback userFeedback);


    Long updateUserFeedback(UserFeedback userFeedback);


    boolean deleteByIds(String ids);


    UserFeedback getUserFeedback(Long id);


    IPage<UserFeedback> getUserFeedbackList(Integer pageNum, Integer pageSize);

}
