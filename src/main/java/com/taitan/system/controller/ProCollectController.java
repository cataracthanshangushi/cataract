package com.taitan.system.controller;

import com.taitan.system.common.result.Result;
import com.taitan.system.pojo.entity.ProCollect;
import com.taitan.system.pojo.entity.ProContact;
import com.taitan.system.pojo.vo.FileInfoVO;
import com.taitan.system.service.ProCollectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "收藏接口")
@RestController
@RequestMapping("/api/v1/ProCollect")
@RequiredArgsConstructor
public class ProCollectController {

    private final ProCollectService proCollectService;

    @PostMapping("/addCollect")
    @Operation(summary = "添加收藏", security = {@SecurityRequirement(name = "Authorization")})
    public Result addCollect(
            @RequestBody @Valid ProCollect proCollect
    ) {
        boolean result = proCollectService.saveProCollect(proCollect);
        return Result.judge(result);
    }

    @PostMapping("/delectCollect")
    @Operation(summary = "删除收藏", security = {@SecurityRequirement(name = "Authorization")})
    public Result delectCollect(
            @RequestBody @Valid ProCollect proCollect
    ) {
        boolean result = proCollectService.deleteProCollect(proCollect);
        return Result.judge(result);
    }

    @GetMapping("/selectProCollectByuserId")
    @Operation(summary = "查询用户收藏的产品", security = {@SecurityRequirement(name = "Authorization")})
    public Result selectProCollectByuserId(
            @Parameter(description = "用户ID") Long id
    ) {
        List<ProCollect> result = proCollectService.getProCollectByUserId(id);
        return Result.success(result);
    }

}
