package com.taitan.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taitan.system.pojo.entity.SysDict;
import com.taitan.system.pojo.form.DictForm;
import com.taitan.system.pojo.vo.DictPageVO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

/**
 * 字典数据项对象转换器
 *
 * @author haoxr
 * @date 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface DictConverter {

    Page<DictPageVO> entity2Page(Page<SysDict> page);

    DictForm entity2Form(SysDict entity);

    @InheritInverseConfiguration(name="entity2Form")
    SysDict form2Entity(DictForm entity);
}
