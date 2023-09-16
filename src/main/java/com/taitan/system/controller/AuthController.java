package com.taitan.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.taitan.system.common.constant.SecurityConstants;
import com.taitan.system.common.result.Result;
import com.taitan.system.common.util.RequestUtils;
import com.taitan.system.framework.easycaptcha.service.EasyCaptchaService;
import com.taitan.system.pojo.dto.CaptchaResult;
import com.taitan.system.pojo.dto.LoginResult;
import com.taitan.system.framework.security.JwtTokenManager;
import com.taitan.system.pojo.form.UserForm;
import com.taitan.system.service.SysUserService;
import com.taitan.system.pojo.entity.SysUser;
import com.taitan.system.service.SysUserService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Tag(name = "01.认证中心")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;
    private final EasyCaptchaService easyCaptchaService;
    private final RedisTemplate redisTemplate;
    private final SysUserService userService;
    private final SysUserService userService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResult> login(
            @Parameter(description = "用户名", example = "admin") @RequestParam String username,
            @Parameter(description = "密码", example = "123456") @RequestParam String password
    ) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username.toLowerCase().trim(),
                password
        );
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        // 生成token
        String accessToken = jwtTokenManager.createToken(authentication);
        LoginResult loginResult = LoginResult.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)

                .build();
        return Result.success(loginResult);
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<LoginResult> register(
            @Parameter(description = "用户名", example = "admin") @RequestParam String username,
            @Parameter(description = "密码", example = "123456") @RequestParam String password
    ) {
        UserForm userForm = new UserForm();
        userForm.setUsername(username);
        userForm.setPassword(password);
        userForm.setRoleIds(List.of(1L));
        userForm.setNickname(username);
        boolean result = userService.saveUser(userForm);
        if(result){
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    username.toLowerCase().trim(),
                    password
            );
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            // 生成token
            String accessToken = jwtTokenManager.createToken(authentication);
            LoginResult loginResult = LoginResult.builder()
                    .tokenType("Bearer")
                    .accessToken(accessToken)

                    .build();
            return Result.success(loginResult);
        }else {
            return Result.failed("注册失败");
        }

    }

    @Operation(summary = "注销", security = {@SecurityRequirement(name = SecurityConstants.TOKEN_KEY)})
    @DeleteMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String token = RequestUtils.resolveToken(request);
        if (StrUtil.isNotBlank(token)) {
            Claims claims = jwtTokenManager.getTokenClaims(token);
            String jti = claims.get("jti", String.class);

            Date expiration = claims.getExpiration();
            if (expiration != null) {
                // 有过期时间，在token有效时间内存入黑名单，超出时间移除黑名单节省内存占用
                long ttl = (expiration.getTime() - System.currentTimeMillis());
                redisTemplate.opsForValue().set(SecurityConstants.BLACK_TOKEN_CACHE_PREFIX + jti, null, ttl, TimeUnit.MILLISECONDS);
            } else {
                // 无过期时间，永久加入黑名单
                redisTemplate.opsForValue().set(SecurityConstants.BLACK_TOKEN_CACHE_PREFIX + jti, null);
            }
        }
        SecurityContextHolder.clearContext();
        return Result.success("注销成功");
    }
    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public Result getCaptcha() {
        CaptchaResult captcha = easyCaptchaService.getCaptcha();
        return Result.success(captcha);
    }

    @Operation(summary = "用户修改密码")
    @PostMapping("/updatePassword")
    public Result updatePassword(
            @Parameter(description = "用户名", example = "admin") @RequestParam String username,
            @Parameter(description = "密码", example = "123456") @RequestParam String password,
            @Parameter(description = "新密码", example = "123456789") @RequestParam String newPassword
    ) {
        boolean result=false;
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username.toLowerCase().trim(),
                password
        );
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if(authentication.isAuthenticated()){
            result = userService.update(new LambdaUpdateWrapper<SysUser>()
                    .eq(SysUser::getUsername, username)
                    .set(SysUser::getPassword, passwordEncoder.encode(newPassword))
            );
        }
        return Result.judge(result);

    }

}
