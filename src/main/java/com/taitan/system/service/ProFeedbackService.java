package com.taitan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.taitan.system.pojo.entity.ProFeedback;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author PB080086
 * @since 2023-09-19
 */
public interface ProFeedbackService extends IService<ProFeedback> {

    Long saveProFeedback(ProFeedback proFeedback);


    Long updateProFeedback(ProFeedback proFeedback);


    boolean deleteByIds(String ids);


    ProFeedback getProFeedback(Long id);


    IPage<ProFeedback> getProFeedbackList(Long userId,Long contactId,Long proId,String proName,Integer pageNum, Integer pageSize);

}
