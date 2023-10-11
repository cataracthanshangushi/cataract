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

    @PostMapping("/saveProDetail")
    @Operation(summary = "新增产品信息", security = {@SecurityRequirement(name = "Authorization")})
    public Result saveProDetail(
            @RequestBody @Valid ProductDetail productDetail
    ) {
        Long updateResult = productDetailService.saveProductDetail(productDetail);
        return Result.success(updateResult);
    }

}
