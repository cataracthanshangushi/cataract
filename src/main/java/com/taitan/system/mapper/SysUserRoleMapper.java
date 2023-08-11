package com.taitan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taitan.system.pojo.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色持久层
 *
 * @author haoxr
 * @date 2022/1/15
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

}
