package com.taitan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taitan.system.pojo.entity.ProContact;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.pojo.form.DeptForm;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author PB080086
 * @since 2023-09-19
 */
public interface ProductDetailService extends IService<ProductDetail> {
    Long saveProductDetail(ProductDetail productDetail);


    Long updateProductDetail(Long id, ProductDetail productDetail);


    boolean deleteByIds(String ids);


    ProductDetail getProductDetail(Long id);

}
