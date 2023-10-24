package com.taitan.system.service.impl;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taitan.system.mapper.ProductDetailMapper;
import com.taitan.system.pojo.bo.UserAuthInfo;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.pojo.entity.SysUser;
import com.taitan.system.pojo.vo.ProductDetailVO;
import com.taitan.system.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
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
    public Long updateProductDetail(ProductDetail productDetail) {
        long count = this.count(new LambdaQueryWrapper<ProductDetail>()
                .eq(ProductDetail::getId, productDetail.getId())
        );
        Assert.isTrue(count != 0, "产品ID不存在");
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
    public IPage<ProductDetail> getProDetailByUserId(Long userid, Integer pageNum,Integer pageSize) {
        IPage<ProductDetail> page = new Page(pageNum, pageSize);
        QueryWrapper<ProductDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userid);
        IPage<ProductDetail> productList =this.page(page,wrapper);
        //List<ProductDetail> productList = productDetailMapper.selectList(wrapper);
        return productList;
    }

    @Override
    public Map<Integer, List<ProductDetailVO>> getProDetail() {
        List<ProductDetailVO> productList = productDetailMapper.getProDetail();
        Map<Integer, List<ProductDetailVO>> collect = productList.stream().collect(Collectors.groupingBy(ProductDetailVO::getCategory));
        return collect;
    }

    @Override
    public List<ProductDetailVO> getNewProDetail() {
        return productDetailMapper.getNewProDetail();
    }

    @Override
    public IPage<ProductDetailVO> getProDetailByName(Integer pageNum, Integer pageSize, String name) {
        IPage<ProductDetailVO> page = new Page(pageNum, pageSize);
        IPage<ProductDetailVO> productList = productDetailMapper.getProDetailByName(page, name);
        return productList;
    }

    @Override
    public IPage<ProductDetail> getProDetailVague(Integer pageNum, Integer pageSize, String name, Long category, Integer online) {
        IPage<ProductDetail> page = new Page(pageNum, pageSize);
        IPage<ProductDetail> productList =this.page(page,new QueryWrapper<ProductDetail>().lambda()
                .like(StrUtil.isNotBlank(name),ProductDetail::getProductName, name)
                .eq(ObjectUtil.isNotEmpty(category),ProductDetail::getCategory,category)
                .eq(ObjectUtil.isNotEmpty(online),ProductDetail::getOnline,online)
        );
        return productList;
    }
}
