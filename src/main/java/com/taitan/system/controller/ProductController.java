package com.taitan.system.controller;

import com.taitan.system.common.result.PageResult;
import com.taitan.system.common.result.Result;
import com.taitan.system.pojo.entity.ProContact;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.pojo.form.UserForm;
import com.taitan.system.pojo.vo.FileInfoVO;
import com.taitan.system.service.ProContactService;
import com.taitan.system.service.ProductDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/updateProDetail")
    @Operation(summary = "更新产品信息", security = {@SecurityRequirement(name = "Authorization")})
    public Result updateProDetail(
            @RequestBody @Valid ProductDetail productDetail
    ) {
        Long updateResult = productDetailService.updateProductDetail(productDetail);
        return Result.success(updateResult);
    }

}
