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
@TableName("product_detail")
@Data
public class ProductDetail extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
//    @NotNull(message = "ID不能为空")
    private Long id;

    @NotNull(message = "用户名ID不能为空")
    private Long userId;

    @NotBlank(message = "名称不能为空")
    private String productName;

    @NotNull(message = "名片ID不能为空")
    private Long contactId;

    @NotNull(message = "类别不能为空")
    private Long category;
    private Integer display;
    private String cover;
    private String proIntroduction;
    private Integer status;
    private String proParameters;
    private String proApplication;

}
