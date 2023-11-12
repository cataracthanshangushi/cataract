package com.taitan.system.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.taitan.system.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description ="首页产品")
@Data
public class ProductDetailVO extends BaseEntity {
    private Long id;
    private Long userId;
    private String productName;
    private Long contactId;
    private String cover;
    private Integer category;
    private String subheading;
}
