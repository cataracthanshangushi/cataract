package com.taitan.system.framework.counters.interceptor;

import com.taitan.system.framework.counters.service.CounterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CounterInterceptor implements HandlerInterceptor {
    private CounterService counterService;

    public CounterInterceptor(CounterService counterService) {
        this.counterService = counterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        counterService.increment(path);
        return true;
    }
}
