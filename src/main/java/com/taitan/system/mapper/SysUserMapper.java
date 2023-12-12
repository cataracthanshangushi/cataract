package com.taitan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taitan.system.framework.mybatisplus.DataPermission;
import com.taitan.system.pojo.bo.UserBO;
import com.taitan.system.pojo.entity.SysUser;
import com.taitan.system.pojo.bo.UserAuthInfo;
import com.taitan.system.pojo.bo.UserFormBO;
import com.taitan.system.pojo.query.UserPageQuery;
import com.taitan.system.pojo.vo.UserExportVO;
import com.taitan.system.pojo.vo.UserInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户持久层
 *
 * @author haoxr
 * @date 2022/1/14
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 获取用户分页列表
     *
     * @param page
     * @param queryParams 查询参数
     * @return
     */
    @DataPermission(deptAlias = "u")
    Page<UserBO> getUserPage(Page<UserBO> page, UserPageQuery queryParams);

    /**
     * 获取用户表单详情
     *
     * @param userId 用户ID
     * @return
     */
    UserFormBO getUserDetail(Long userId);

    /**
     * 根据用户名获取认证信息
     *
     * @param username
     * @return
     */
    UserAuthInfo getUserAuthInfo(String username);

    /**
     * 获取导出用户列表
     *
     * @param queryParams
     * @return
     */
    @DataPermission(deptAlias = "u")
    List<UserExportVO> listExportUsers(UserPageQuery queryParams);

    @Select("select id as userId,username,nickname,avatar,birthday,gender,email,occupation,profession,dept_id,mobile from sys_user where username " +
            "like CONCAT('%',#{name},'%')")
    IPage<UserInfoVO> getListUsers(IPage<UserInfoVO> page, String name);
}
