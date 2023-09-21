package com.taitan.system.pojo.vo;

import com.taitan.system.pojo.entity.ProContact;
import com.taitan.system.pojo.entity.ProductDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户登录视图对象
 *
 * @author haoxr
 * @date 2022/1/14
 */
@Schema(description ="当前登录用户视图对象")
@Data
public class UserInfoVO {

    @Schema(description="用户ID")
    private Long userId;

    @Schema(description="用户昵称")
    private String nickname;

    @Schema(description="用户名")
    private String username;

    @Schema(description="头像地址")
    private String avatar;

    @Schema(description="用户生日")
    private String birthday;

    @Schema(description="用户职业")
    private String occupation;

    @Schema(description="用户职称")
    private String profession;

    @Schema(description="产品人信息")
    private List<ProContact> proContact;

    @Schema(description="产品信息")
    private List<ProductDetail> productDetail;

//    @Schema(description="用户角色编码集合")
//    private Set<String> roles;
//
//    @Schema(description="用户权限标识集合")
//    private Set<String> perms;

}
