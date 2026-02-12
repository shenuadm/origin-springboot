package com.cosmos.origin.gateway.interceptor;

import com.cosmos.origin.common.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.Instant;

/**
 * 访问日志拦截器
 *
 * @author cosmos
 */
@Slf4j
@Component
public class AccessLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME_KEY = "startTime";

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  处理器对象
     * @return boolean 是否继续处理请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        request.setAttribute(START_TIME_KEY, Instant.now());
        return true;
    }

    /**
     * 在请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     *
     * @param request      请求对象
     * @param response     响应对象
     * @param handler      处理器对象
     * @param modelAndView 视图对象
     */
    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                           @NonNull Object handler, ModelAndView modelAndView) {
        // 不需要处理
    }

    /**
     * 在整个请求处理完毕之后进行调用，包括视图渲染完毕
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  处理器对象
     * @param ex       异常对象
     */
    @Override
    public void afterCompletion(HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        Instant startTime = (Instant) request.getAttribute(START_TIME_KEY);
        if (startTime != null) {
            long duration = Duration.between(startTime, Instant.now()).toMillis();
            log.info("[AccessLog] {} {} {} {}ms - {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    RequestUtil.getClientIp(request));
        }
    }

}
