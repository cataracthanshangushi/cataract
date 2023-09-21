package com.taitan.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taitan.system.mapper.ProductDetailMapper;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.pojo.form.DeptForm;
import com.taitan.system.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author PB080086
 * @since 2023-09-19
 */
@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl extends ServiceImpl<ProductDetailMapper, ProductDetail> implements ProductDetailService {

    @Override
    public Long saveProductDetail(ProductDetail productDetail) {
        return null;
    }

    @Override
    public Long updateProductDetail(Long id, ProductDetail productDetail) {
        return null;
    }

    @Override
    public boolean deleteByIds(String ids) {
        return false;
    }

    @Override
    public ProductDetail getProductDetail(Long id) {
        return null;
    }
}
