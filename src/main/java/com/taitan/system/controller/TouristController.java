package com.taitan.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.taitan.system.common.result.Result;
import com.taitan.system.pojo.entity.ProContact;
import com.taitan.system.pojo.entity.ProductDetail;
import com.taitan.system.pojo.entity.UserFeedback;
import com.taitan.system.pojo.vo.ProductDetailVO;
import com.taitan.system.service.ProContactService;
import com.taitan.system.service.ProductDetailService;
import com.taitan.system.service.UserFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Tag(name = "游客能访问的产品接口")
@RestController
@RequestMapping("/api/v1/tourist")
@RequiredArgsConstructor
public class TouristController {

    private final ProContactService proContactService;

    private final ProductDetailService productDetailService;

    private final UserFeedbackService userFeedbackService;

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
            @Parameter(description = "登录人ID") Long id,
            @Parameter(description = "名片ID") Long contactId,
            @Parameter(description = "上线状态") Integer online,
            @Parameter(description = "当前页码") Integer pageNum,
            @Parameter(description = "每页条数") Integer pageSize
    ) {
        IPage<ProductDetail> result = productDetailService.getProDetailByUserId(id,contactId,online,pageNum,pageSize);
        return Result.success(result);
    }

    @GetMapping("/proDetail")
    @Operation(summary = "首页产品")
    public Result proDetail() {
        Map<Integer, List<ProductDetailVO>> result = productDetailService.getProDetail();
        return Result.success(result);
    }

    @GetMapping("/newProDetail")
    @Operation(summary = "首页最新产品")
    public Result newProDetail() {
        List<ProductDetailVO> result = productDetailService.getNewProDetail();
        return Result.success(result);
    }
    @GetMapping("/getProDetailByDisplay")
    @Operation(summary = "根据显示状态查询产品")
    public Result getProDetailByDisplay(
            @Parameter(description = "显示状态") Integer display
    ) {
        List<ProductDetailVO> result = productDetailService.getProDetailByDisPlay(display);
        return Result.success(result);
    }
    @GetMapping("/getProDetailByName")
    @Operation(summary = "模糊查询产品")
    public Result getProDetailByName(
            @Parameter(description = "产品名字") String productName,
            @Parameter(description = "产品备注") String subhead,
            @Parameter(description = "是否上线") Integer online,
            @Parameter(description = "当前页码") Integer pageNum,
            @Parameter(description = "每页条数") Integer pageSize
    ) {
        IPage<ProductDetailVO> result = productDetailService.getProDetailByName(pageNum,pageSize,productName,subhead,online);
        return Result.success(result);
    }

    @GetMapping("/getProDetailVague")
    @Operation(summary = "多条件模糊查询产品")
    public Result getProDetailVague(
            @Parameter(description = "产品名字") String productName,
            @Parameter(description = "当前页码") Integer pageNum,
            @Parameter(description = "每页条数") Integer pageSize,
            @Parameter(description = "产品分类") Long category,
            @Parameter(description = "上线状态") Integer online,
            @Parameter(description = "用户ID") Long userid
    ) {
        IPage<ProductDetail> result = productDetailService.getProDetailVague(pageNum,pageSize,productName,category,online,userid);
        return Result.success(result);
    }

    @PostMapping("/submitComment")
    @Operation(summary = "提交意见")
    public Result submitComment(
            @RequestBody @Valid UserFeedback userFeedback
    ) {
        Long result = userFeedbackService.saveUserFeedback(userFeedback);
        return Result.success(result);
    }

    @GetMapping("/deleteComment")
    @Operation(summary = "删除意见")
    public Result deleteComment(
            @Parameter(description = "id") String ids
    ) {
        boolean result = userFeedbackService.deleteByIds(ids);
        return Result.judge(result);
    }

    @GetMapping("/selectComment")
    @Operation(summary = "查询意见")
    public Result selectComment(
            @Parameter(description = "当前页码") Integer pageNum,
            @Parameter(description = "每页条数") Integer pageSize
    ) {
        IPage<UserFeedback> result = userFeedbackService.getUserFeedbackList(pageNum,pageSize);
        return Result.success(result);
    }
}
