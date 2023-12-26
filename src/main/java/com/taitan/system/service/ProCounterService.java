package com.taitan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taitan.system.pojo.entity.ProCounter;



/**
 * <p>
 *  服务类
 * </p>
 *
 * @author PB080086
 * @since 2023-09-19
 */
public interface ProCounterService extends IService<ProCounter> {

    Long saveProCounter(ProCounter proCounter);


    Long updateProCounter(ProCounter proCounter);


    boolean deleteByIds(String ids);

    ProCounter getProCounter(Long id);

    Long addProCounter(Long proId);


}
