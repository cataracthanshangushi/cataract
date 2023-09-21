package com.taitan.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taitan.system.mapper.ProContactMapper;
import com.taitan.system.pojo.entity.ProContact;
import com.taitan.system.service.ProContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
public class ProContactServiceImpl extends ServiceImpl<ProContactMapper, ProContact> implements ProContactService {


    @Override
    public Long saveProContact(ProContact proContact) {
        return null;
    }

    @Override
    public Long updateProContact(Long id, ProContact proContact) {
        return null;
    }

    @Override
    public boolean deleteByIds(String ids) {
        return false;
    }

    @Override
    public ProContact getProContact(Long id) {
        return null;
    }


}
