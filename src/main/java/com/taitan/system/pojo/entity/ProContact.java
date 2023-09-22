package com.taitan.system.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.taitan.system.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * <p>
 *
 * </p>
 *
 * @author zoubo
 * @since 2023-09-19
 */
@TableName("pro_contact")
@Data
public class ProContact extends BaseEntity {


    @TableId(value = "id", type = IdType.AUTO)
    @NotNull(message = "ID不能为空")
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;
    private String contactTel;
    private String contactPhone;
    private String companyName;

    @NotBlank(message = "户名不能为空")
    private String contactName;
    private String contactWechat;
    private String contactPost;
    private String comIntroduction;

}
