package com.taitan.system.controller;


import com.taitan.system.common.result.Result;
import com.taitan.system.pojo.entity.ProContact;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.service.ProContactService;
import com.taitan.system.service.ProductDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "产品接口")
@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProContactService proContactService;

    private final ProductDetailService productDetailService;

    @PostMapping("/updateProContact")
    @Operation(summary = "更新产品人", security = {@SecurityRequirement(name = "Authorization")})
    public Result updateProContact(
            @RequestBody @Valid ProContact proContact
    ) {
        Long updateResult = proContactService.updateProContact(proContact);
        return Result.success(updateResult);
    }

    @GetMapping("/delectProConById")
    @Operation(summary = "删除产品人", security = {@SecurityRequirement(name = "Authorization")})
    public Result delectProConById(
            @Parameter(description = "产品人ID") String ids
    ) {
        boolean result = proContactService.deleteByIds(ids);
        return Result.judge(result);
    }

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

    @PostMapping("/saveProContact")
    @Operation(summary = "新增产品人", security = {@SecurityRequirement(name = "Authorization")})
    public Result saveProContact(
            @RequestBody @Valid ProContact proContact
    ) {
        Long updateResult = proContactService.saveProContact(proContact);
        return Result.success(updateResult);
    }

    @PostMapping("/updateProDetail")
    @Operation(summary = "更新产品信息", security = {@SecurityRequirement(name = "Authorization")})
    public Result updateProDetail(
            @RequestBody @Valid ProductDetail productDetail
    ) {
        Long updateResult = productDetailService.updateProductDetail(productDetail);
        return Result.success(updateResult);
    }

    @GetMapping("/delectProDetailById")
    @Operation(summary = "删除产品", security = {@SecurityRequirement(name = "Authorization")})
    public Result delectProDetailById(
            @Parameter(description = "产品ID") String ids
    ) {
        boolean result = productDetailService.deleteByIds(ids);
        return Result.judge(result);
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

    @PostMapping("/saveProDetail")
    @Operation(summary = "新增产品信息", security = {@SecurityRequirement(name = "Authorization")})
    public Result saveProDetail(
            @RequestBody @Valid ProductDetail productDetail
    ) {
        Long updateResult = productDetailService.saveProductDetail(productDetail);
        return Result.success(updateResult);
    }

    @GetMapping("/proDetail")
    @Operation(summary = "首页产品", security = {@SecurityRequirement(name = "Authorization")})
    public Result proDetail() {
        List<ProductDetail> result = productDetailService.getProDetail();
        return Result.success(result);
    }
}
