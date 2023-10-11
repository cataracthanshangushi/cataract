package com.taitan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.pojo.vo.ProductDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author PB080086
 * @since 2023-09-19
 */
@Mapper
public interface ProductDetailMapper extends BaseMapper<ProductDetail> {

    @Select("select id,product_name,user_id,contact_id,cover from product_detail where display = #{display}")
    List<ProductDetailVO> getProDetail(Integer display);

}
