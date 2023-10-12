package com.taitan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.pojo.vo.ProductDetailVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author PB080086
 * @since 2023-09-19
 */
public interface ProductDetailService extends IService<ProductDetail> {
    Long saveProductDetail(ProductDetail productDetail);


    Long updateProductDetail(ProductDetail productDetail);


    boolean deleteByIds(String ids);


    ProductDetail getProductDetail(Long id);


    List<ProductDetail> getProDetailByUserId(Long userid);


    List<ProductDetailVO> getProDetail();

    IPage<ProductDetailVO> getProDetailByName(Integer pageNum, Integer pageSize, String name);

}
