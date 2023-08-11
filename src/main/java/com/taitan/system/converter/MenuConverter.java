package com.taitan.system.converter;

import com.taitan.system.pojo.entity.SysMenu;
import com.taitan.system.pojo.form.MenuForm;
import com.taitan.system.pojo.vo.MenuVO;
import org.mapstruct.Mapper;

/**
 * 菜单对象转换器
 *
 * @author haoxr
 * @date 2022/7/29
 */
@Mapper(componentModel = "spring")
public interface MenuConverter {

    MenuVO entity2Vo(SysMenu entity);


    MenuForm entity2Form(SysMenu entity);

    SysMenu form2Entity(MenuForm menuForm);

}