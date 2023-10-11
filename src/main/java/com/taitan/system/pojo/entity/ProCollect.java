package com.taitan.system.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户和产品关联表
 */
@TableName("pro_collect")
@Data
public class ProCollect {
    /**
     * 产品ID
     */
    @NotNull(message = "ID不能为空")
    private Long proId;

    /**
     * 用户ID
     */
    @NotNull(message = "ID不能为空")
    private Long userId;

}