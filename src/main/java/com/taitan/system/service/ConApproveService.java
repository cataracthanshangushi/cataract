package com.taitan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.taitan.system.pojo.entity.ConApprove;
import com.taitan.system.pojo.vo.UserPageVO;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author PB080086
 * @since 2023-09-19
 */
public interface ConApproveService extends IService<ConApprove> {

    Long saveConApprove(ConApprove conApprove);


    Long updateConApprove(ConApprove conApprove);


    boolean deleteByIds(String ids);


    ConApprove getConApprove(Long id);


    List<ConApprove> getConApproveByUserId(Long userid);


    IPage<ConApprove> getAllConApprove(Integer pageNum, Integer pageSize,Integer statue);

}
