package com.taitan.system.service.impl;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taitan.system.mapper.ProContactMapper;
import com.taitan.system.mapper.ProductDetailMapper;
import com.taitan.system.pojo.entity.ProContact;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.pojo.form.DeptForm;
import com.taitan.system.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private final ProductDetailMapper productDetailMapper;

    @Override
    public Long saveProductDetail(ProductDetail productDetail) {
        this.save(productDetail);
        return productDetail.getId();
    }

    @Override
    public Long updateProductDetail(Long id, ProductDetail productDetail) {
        this.updateById(productDetail);
        return productDetail.getId();
    }

    @Override
    public boolean deleteByIds(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "删除的产品人数据为空");
        // 逻辑删除
        List<Long> ids = Arrays.asList(idsStr.split(",")).stream()
                .map(idStr -> Long.parseLong(idsStr)).collect(Collectors.toList());
        boolean result = this.removeByIds(ids);
        return result;
    }

    @Override
    public ProductDetail getProductDetail(Long id) {
        ProductDetail productDetail = this.getOne(new LambdaQueryWrapper<ProductDetail>()
                .eq(ProductDetail::getId, id)
                .select()
        );
        return productDetail;
    }

    @Override
    public List<ProductDetail> getProDetailByUserId(Long userid) {
        QueryWrapper<ProductDetail> wrapper=new QueryWrapper<>();
        wrapper.eq("user_id",userid);
        List<ProductDetail> userList = productDetailMapper.selectList(wrapper);
        return userList;
    }
}
