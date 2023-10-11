package com.taitan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taitan.system.pojo.entity.ProCollect;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zou
 * @since 2023-09-19
 */
public interface ProCollectService extends IService<ProCollect> {

    boolean saveProCollect(ProCollect proCollect);


    boolean deleteProCollect(ProCollect proCollect);


    List<ProCollect> getProCollectByUserId(Long id);


}
