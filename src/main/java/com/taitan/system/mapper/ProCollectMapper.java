package com.taitan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taitan.system.pojo.entity.ProCollect;
import com.taitan.system.pojo.vo.ProductDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zou
 * @since 2023-09-19
 */
@Mapper
public interface ProCollectMapper extends BaseMapper<ProCollect> {

    @Select("select b.id,b.user_id,b.product_name,b.contact_id,b.cover,b.category,b.subheading,b.online,b.status " +
            "from pro_collect a LEFT JOIN product_detail b ON a.pro_id=b.id WHERE a.user_id=#{userId}")
    List<ProductDetailVO> getProDetailByCol(Long userId);

}
