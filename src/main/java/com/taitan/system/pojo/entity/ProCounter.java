package com.taitan.system.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 收藏量和产品关联表
 */
@TableName("pro_counters")
@Data
public class ProCounter {
    /**
     * 产品ID
     */
    @NotNull(message = "ID不能为空")
    private Long proId;

    private Long proViews;

    private Long proCollection;

}