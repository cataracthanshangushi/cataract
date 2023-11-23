package com.taitan.system.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.taitan.system.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;

import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author zoubo
 * @since 2023-09-19
 */
@TableName("booth_order")
@Data
public class BoothOrder extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "订单ID不能为空")
    private String orderId;

    private Integer boothNum;

    private Decimal boothPrice;

    private Integer boothStatus;

    private Date boothExpiration;

}
