package com.taitan.system.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.taitan.system.common.base.BaseEntity;
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
@TableName("user_feedback")
@Data
public class UserFeedback extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String type;

    private String description;

    private String mobile;

}
