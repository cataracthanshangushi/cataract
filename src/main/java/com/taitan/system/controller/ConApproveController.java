package com.taitan.system.controller;

import com.taitan.system.common.result.Result;
import com.taitan.system.pojo.entity.ConApprove;
import com.taitan.system.service.ConApproveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "审核接口")
@RestController
@RequestMapping("/api/v1/Approve")
@RequiredArgsConstructor
public class ConApproveController {

    private final ConApproveService conApproveService;

    @PostMapping("/addApprove")
    @Operation(summary = "提交审核", security = {@SecurityRequirement(name = "Authorization")})
    public Result addApprove(
            @RequestBody @Valid ConApprove conApprove
    ) {
        Long result = conApproveService.saveConApprove(conApprove);
        return Result.success(result);
    }

    @PostMapping("/updateApprove")
    @Operation(summary = "更新审核", security = {@SecurityRequirement(name = "Authorization")})
    public Result updateApprove(
            @RequestBody @Valid ConApprove conApprove
    ) {
        Long result = conApproveService.updateConApprove(conApprove);
        return Result.success(result);
    }

    @GetMapping("/delectApprove")
    @Operation(summary = "删除审核", security = {@SecurityRequirement(name = "Authorization")})
    public Result delectApprove(
            @Parameter(description = "审核id") String ids
    ) {
        boolean result = conApproveService.deleteByIds(ids);
        return Result.judge(result);
    }

    @GetMapping("/selectApproveByuserId")
    @Operation(summary = "通过登录用户查询审核信息", security = {@SecurityRequirement(name = "Authorization")})
    public Result selectApproveByuserId(
            @Parameter(description = "用户ID") Long id
    ) {
        List<ConApprove> result = conApproveService.getConApproveByUserId(id);
        return Result.success(result);
    }

}
