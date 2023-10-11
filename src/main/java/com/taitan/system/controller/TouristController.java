package com.taitan.system.controller;


import com.taitan.system.common.result.Result;
import com.taitan.system.pojo.entity.ProContact;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.pojo.vo.ProductDetailVO;
import com.taitan.system.service.ProContactService;
import com.taitan.system.service.ProductDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "游客能访问的产品接口")
@RestController
@RequestMapping("/api/v1/tourist")
@RequiredArgsConstructor
public class TouristController {

    private final ProContactService proContactService;

    private final ProductDetailService productDetailService;

    @GetMapping("/selectProConById")
    @Operation(summary = "选择产品人", security = {@SecurityRequirement(name = "Authorization")})
    public Result selectProConById(
            @Parameter(description = "产品人ID") Long id
    ) {
        ProContact result = proContactService.getProContact(id);
        return Result.success(result);
    }

    @GetMapping("/selectProConByUserId")
    @Operation(summary = "通过登录人获取产品人", security = {@SecurityRequirement(name = "Authorization")})
    public Result selectProConByUserId(
            @Parameter(description = "产品人ID") Long id
    ) {
        List<ProContact> result = proContactService.getProContactByUserId(id);
        return Result.success(result);
    }

    @GetMapping("/selectProDetailById")
    @Operation(summary = "选择产品", security = {@SecurityRequirement(name = "Authorization")})
    public Result selectProDetailById(
            @Parameter(description = "产品ID") Long id
    ) {
        ProductDetail result = productDetailService.getProductDetail(id);
        return Result.success(result);
    }

    @GetMapping("/selectProDetailByUserId")
    @Operation(summary = "通过登录人获取产品", security = {@SecurityRequirement(name = "Authorization")})
    public Result selectProDetailByUserId(
            @Parameter(description = "产品ID") Long id
    ) {
        List<ProductDetail> result = productDetailService.getProDetailByUserId(id);
        return Result.success(result);
    }

    @GetMapping("/proDetail")
    @Operation(summary = "首页产品")
    public Result proDetail() {
        List<ProductDetailVO> result = productDetailService.getProDetail();
        return Result.success(result);
    }
}