package com.taitan.system.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.taitan.system.common.base.BaseEntity;
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
    private Integer id;
    private String productName;
    private String cover;
    private String proIntroduction;
    private String proParameters;
    private String proApplication;

}
