package com.taitan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taitan.system.pojo.entity.ProContact;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author PB080086
 * @since 2023-09-19
 */
public interface ProContactService extends IService<ProContact> {

    Long saveProContact(ProContact proContact);


    Long updateProContact(Long id, ProContact proContact);


    boolean deleteByIds(String ids);


    ProContact getProContact(Long id);


    List<ProContact> getProContactByUserId(Long userid);

}
