package com.taitan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taitan.system.pojo.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色菜单持久层
 *
 * @author haoxr
 * @date 2022/6/4
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 获取角色拥有的菜单ID集合
     *
     * @param roleId
     * @return
     */
    List<Long> listMenuIdsByRoleId(Long roleId);
}
