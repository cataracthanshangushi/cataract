package com.taitan.system.controller;

import com.taitan.system.common.constant.SecurityConstants;
import com.taitan.system.common.result.Result;
import com.taitan.system.framework.security.JwtTokenManager;
import com.taitan.system.pojo.dto.LoginResult;
import com.taitan.system.service.AliSmsService;
import com.taitan.system.service.ProductDetailService;
import com.taitan.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@Tag(name = "短信操作接口")
@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class SendMsController {

    private final AliSmsService aliSmsService;

    private final SysUserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenManager jwtTokenManager;

    private final ProductDetailService productDetailService;

    @Operation(summary = "短信登录")
    @PostMapping("/login")
    public Result login(
            @Parameter(description = "用户名") @RequestParam String phone,
            @Parameter(description = "验证码") @RequestParam String code
    ) {
        if(userService.checkUserName(phone)){
            if(aliSmsService.checkCode(phone,code)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        phone.toLowerCase().trim(),
                        SecurityConstants.CUSTOM_LOGIN_SMS
                );
                Authentication authentication = authenticationManager.authenticate(authenticationToken);
                // 生成token
                String accessToken = jwtTokenManager.createToken(authentication);
                LoginResult loginResult = LoginResult.builder()
                        .tokenType("Bearer")
                        .accessToken(accessToken)
                        .build();
                return Result.success(loginResult);
            }else{
                return Result.failed("验证码错误");
            }
        }else{
            return Result.failed("用户不存在");
        }
    }

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
