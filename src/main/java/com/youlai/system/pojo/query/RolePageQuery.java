package com.youlai.system.pojo.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色分页查询实体
 *
 * @author haoxr
 * @date 2022/6/3
 *
 */
@Data
public class RolePageQuery extends BasePageQuery {

    @Schema(description="关键字(角色名称/角色编码)")
    private String keywords;
}
