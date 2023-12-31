package com.taitan.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taitan.system.pojo.bo.UserBO;
import com.taitan.system.pojo.entity.SysUser;
import com.taitan.system.pojo.form.UserForm;
import com.taitan.system.pojo.bo.UserFormBO;
import com.taitan.system.pojo.vo.UserImportVO;
import com.taitan.system.pojo.vo.UserInfoVO;
import com.taitan.system.pojo.vo.UserPageVO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * 用户对象转换器
 *
 * @author haoxr
 * @date 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    @Mappings({
            @Mapping(target = "genderLabel", expression = "java(com.taitan.system.common.base.IBaseEnum.getLabelByValue(bo.getGender(), com.taitan.system.common.enums.GenderEnum.class))")
    })
    UserPageVO bo2Vo(UserBO bo);

    Page<UserPageVO> bo2Vo(Page<UserBO> bo);

    UserForm bo2Form(UserFormBO bo);

    UserForm entity2Form(SysUser entity);

    @InheritInverseConfiguration(name = "entity2Form")
    SysUser form2Entity(UserForm entity);

    @Mappings({
            @Mapping(target = "userId", source = "id")
    })
    UserInfoVO entity2UserInfoVo(SysUser entity);

    SysUser importVo2Entity(UserImportVO vo);

}
