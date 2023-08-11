package com.taitan.system.converter;

import com.taitan.system.pojo.entity.SysDept;
import com.taitan.system.pojo.form.DeptForm;
import com.taitan.system.pojo.vo.DeptVO;
import org.mapstruct.Mapper;

/**
 * 部门对象转换器
 *
 * @author haoxr
 * @date 2022/7/29
 */
@Mapper(componentModel = "spring")
public interface DeptConverter {

    DeptForm entity2Form(SysDept entity);
    DeptVO entity2Vo(SysDept entity);

    SysDept form2Entity(DeptForm deptForm);

}