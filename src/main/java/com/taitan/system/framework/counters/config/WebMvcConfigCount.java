/*
package com.taitan.system.framework.counters.config;

import com.taitan.system.framework.counters.interceptor.CounterInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfigCount implements WebMvcConfigurer {
    private CounterInterceptor counterInterceptor;

    public WebMvcConfigCount(CounterInterceptor counterInterceptor) {
        this.counterInterceptor = counterInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(counterInterceptor).addPathPatterns("/api/v1/tourist/**");
    }
}
*/
