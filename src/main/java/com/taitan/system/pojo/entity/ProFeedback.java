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
@TableName("pro_feedback")
@Data
public class ProFeedback extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long contactId;

    private Long proId;

    private String proName;

    private String proDescription;

    private String mobile;

}
