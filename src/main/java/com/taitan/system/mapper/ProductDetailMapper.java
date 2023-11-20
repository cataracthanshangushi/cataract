package com.taitan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.pojo.vo.ProductDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author PB080086
 * @since 2023-09-19
 */
@Mapper
public interface ProductDetailMapper extends BaseMapper<ProductDetail> {

    @Select("select id,product_name,user_id,contact_id,cover,category,subheading,online from (\n" +
            "select *,row_number() over(PARTITION by category order by id) as rowid \n" +
            "from product_detail where display>0 \n" +
            ") a where rowid<=10 ")
    List<ProductDetailVO> getProDetail();

    @Select("select id,product_name,user_id,contact_id,cover,category,subheading,online from product_detail where online = #{online} and (product_name " +
            "like CONCAT('%',#{name},'%') or subheading like CONCAT('%',#{subhead},'%'))")
    IPage<ProductDetailVO> getProDetailByName(IPage<ProductDetailVO> page, String name, String subhead,Integer online);

    @Select("SELECT id,product_name,user_id,contact_id,cover,category,subheading,online FROM product_detail where online=1 and display=0 ORDER BY create_time DESC LIMIT 10")
    List<ProductDetailVO> getNewProDetail();

    @Select("SELECT id,product_name,user_id,contact_id,cover,category,subheading,online FROM product_detail WHERE display=#{display}")
    List<ProductDetailVO> getProDetailByDisPlay(Integer display);

}
