package com.taitan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.pojo.vo.ProductDetailVO;

import java.util.List;
import java.util.Map;

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


    IPage<ProductDetail> getProDetailByUserId(Long userid,Long contactId,Integer online, Integer pageNum,Integer pageSize);


    Map<Integer, List<ProductDetailVO>> getProDetail();

    List<ProductDetailVO> getNewProDetail();

    List<ProductDetailVO> getProDetailByDisPlay(Integer display);

    IPage<ProductDetailVO> getProDetailByName(Integer pageNum, Integer pageSize, String name, String subhead,Integer online);

    IPage<ProductDetail> getProDetailVague(Integer pageNum, Integer pageSize, String name,Long category,Integer online, Long userid);

}
