package com.taitan.system.controller;

import com.taitan.system.common.result.Result;
import com.taitan.system.service.AliSmsService;
import com.taitan.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "短信操作接口")
@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class SendMsController {

    private final AliSmsService aliSmsService;

    private final SysUserService userService;

    @GetMapping("/sendcode")
    @Operation(summary = "发短信")
    public Result sendcode(
            @Parameter(description = "手机号") @RequestParam String phone
    ) {
        return Result.judge(aliSmsService.sendCode(phone));
    }

    @GetMapping("/checkcode")
    @Operation(summary = "验证短信")
    public Result checkcode(
            @Parameter(description = "手机号") @RequestParam String phone,
            @Parameter(description = "验证码") @RequestParam String code
    ) {
        return Result.judge(aliSmsService.checkCode(phone,code));
    }

    @GetMapping("/checkphone")
    @Operation(summary = "验证用户名")
    public Result checkphone(
            @Parameter(description = "手机号") @RequestParam String phone
    ) {
        return Result.judge(userService.checkUserName(phone));
    }
}
