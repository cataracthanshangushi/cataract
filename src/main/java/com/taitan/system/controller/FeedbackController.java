package com.taitan.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.taitan.system.common.result.Result;
import com.taitan.system.pojo.entity.ProFeedback;
import com.taitan.system.pojo.entity.UserFeedback;
import com.taitan.system.service.ProFeedbackService;
import com.taitan.system.service.UserFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "游客能访问的意见接口")
@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final UserFeedbackService userFeedbackService;

    private final ProFeedbackService proFeedbackService;

    @PostMapping("/submitComment")
    @Operation(summary = "提交意见")
    public Result submitComment(
            @RequestBody @Valid UserFeedback userFeedback
    ) {
        Long result = userFeedbackService.saveUserFeedback(userFeedback);
        return Result.success(result);
    }

    @PostMapping("/submitProComment")
    @Operation(summary = "提交产品意见")
    public Result submitProComment(
            @RequestBody @Valid ProFeedback proFeedback
    ) {
        Long result = proFeedbackService.saveProFeedback(proFeedback);
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

    @GetMapping("/deleteProComment")
    @Operation(summary = "删除产品意见")
    public Result deleteProComment(
            @Parameter(description = "id") String ids
    ) {
        boolean result = proFeedbackService.deleteByIds(ids);
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

    @GetMapping("/selectProComment")
    @Operation(summary = "查询产品意见")
    public Result selectProComment(
            @Parameter(description = "用户ID") Long userId,
            @Parameter(description = "名片ID") Long contactId,
            @Parameter(description = "产品ID") Long proId,
            @Parameter(description = "产品名") String proName,
            @Parameter(description = "当前页码") Integer pageNum,
            @Parameter(description = "每页条数") Integer pageSize
    ) {
        IPage<ProFeedback> result = proFeedbackService.getProFeedbackList(userId,contactId,proId,proName,pageNum,pageSize);
        return Result.success(result);
    }
}
