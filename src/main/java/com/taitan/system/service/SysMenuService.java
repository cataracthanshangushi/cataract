package com.taitan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taitan.system.pojo.form.MenuForm;
import com.taitan.system.pojo.vo.Option;
import com.taitan.system.pojo.entity.SysMenu;
import com.taitan.system.pojo.query.MenuQuery;
import com.taitan.system.pojo.vo.MenuVO;
import com.taitan.system.pojo.vo.RouteVO;

import java.util.List;
import java.util.Set;

/**
 * 菜单业务接口
 * 
 * @author haoxr
 * @date 2020/11/06
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取菜单表格列表
     *
     * @return
     */
    List<MenuVO> listMenus(MenuQuery queryParams);


    /**
     * 获取菜单下拉列表
     *
     * @return
     */
    List<Option> listMenuOptions();

    /**
     * 新增菜单
     *
     * @param menu
     * @return
     */
    boolean saveMenu(MenuForm menu);

    /**
     * 获取路由列表
     *
     * @return
     */
    List<RouteVO> listRoutes();

    /**
     * 修改菜单显示状态
     * 
     * @param menuId 菜单ID
     * @param visible 是否显示(1->显示；2->隐藏)
     * @return
     */
    boolean updateMenuVisible(Long menuId, Integer visible);

    /**
     * 获取角色权限集合
     *
     * @param roles
     * @return
     */
    Set<String> listRolePerms(Set<String> roles);

    /**
     * 获取菜单表单数据
     *
     * @param id 菜单ID
     * @return
     */
    MenuForm getMenuForm(Long id);

    /**
     * 删除菜单
     *
     * @param id
     * @return
     */
    boolean deleteMenu(Long id);
}
