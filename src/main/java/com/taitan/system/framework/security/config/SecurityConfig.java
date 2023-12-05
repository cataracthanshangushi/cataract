package com.taitan.system.framework.security.config;

import cn.hutool.extra.spring.SpringUtil;
import com.taitan.system.common.constant.SecurityConstants;
import com.taitan.system.framework.security.filter.CustomLoginAuthenticationProvider;
import com.taitan.system.framework.security.filter.JwtAuthenticationFilter;
import com.taitan.system.framework.security.exception.MyAccessDeniedHandler;
import com.taitan.system.framework.security.exception.MyAuthenticationEntryPoint;
import com.taitan.system.framework.security.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 权限配置
 *
 * @author haoxr
 * @date 2023/2/17
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyAuthenticationEntryPoint myAuthenticationEntryPoint;
    private final MyAccessDeniedHandler myAccessDeniedHandler;
    private final JwtTokenManager jwtTokenManager;
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(SecurityConstants.LOGIN_PATH).permitAll() // 登录接口放行但会走过滤器链-验证码校验
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint)
                .accessDeniedHandler(myAccessDeniedHandler)
        ;

        // 验证码校验过滤器
//        http.addFilterBefore(new VerifyCodeFilter(),UsernamePasswordAuthenticationFilter.class);
        // JWT 校验过滤器
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenManager), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 不走过滤器链的放行配置
     *
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/api/v1/auth/**",
                        "/api/v1/message/**",
                        "/webjars/**",
                        "/doc.html",
                        "/api/v1/tourist/**",
                        "/api/v1/feedback/**",
//                        "/swagger-resources/**",
                        "/v3/api-docs/**"
//                        "/swagger-ui/**"
                );
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 无法直接注入 AuthenticationManager
     *
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        this.userDetailsService= SpringUtil.getBean(UserDetailsService.class);
        DaoAuthenticationProvider authenticationProvider = new CustomLoginAuthenticationProvider(userDetailsService);
        authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;
    }

}
