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
@TableName("pro_contact")
@Data
public class ProContact extends BaseEntity {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String contactTel;
    private String contactPhone;
    private String companyName;
    private String contactName;
    private String contactWechat;
    private String contactPost;
    private String comIntroduction;

}
