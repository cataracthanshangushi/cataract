package com.taitan.system.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.taitan.system.common.base.BaseEntity;
import com.taitan.system.framework.validator.Wildcard;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


/**
 * <p>
 *
 * </p>
 *
 * @author zoubo
 * @since 2023-09-19
 */
@TableName("con_approve")
@Data
public class ConApprove extends BaseEntity {


    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "户名不能为空")
    private String companyName;

    private String lisence;
    private String companyContact;

    @Wildcard(message = "身份证格式不对")
    private String identifyCard;

    @Pattern(regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$", message = "手机号格式不对")
    private String companyTel;

    @Email(message = "邮箱格式不对")
    private String companyEmail;

    private String businessScope;
    private String feedback;
    private Integer status;
}
