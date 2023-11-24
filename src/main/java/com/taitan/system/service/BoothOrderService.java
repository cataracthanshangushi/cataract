package com.taitan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taitan.system.pojo.entity.BoothOrder;
import com.taitan.system.pojo.entity.BoothOrder;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author PB080086
 * @since 2023-09-19
 */
public interface BoothOrderService extends IService<BoothOrder> {

    Long saveBoothOrder(BoothOrder boothOrder);


    Long updateBoothOrder(BoothOrder boothOrder);


    boolean deleteByIds(String ids);


    BoothOrder getBoothOrder(Long id);


    List<BoothOrder> getBoothOrderByUserId(Long userid);

}
