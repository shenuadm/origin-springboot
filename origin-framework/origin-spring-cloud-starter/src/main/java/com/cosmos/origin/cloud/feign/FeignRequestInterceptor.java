package com.cosmos.origin.cloud.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

/**
 * Feign 请求拦截器
 * <p>
 * 在服务间调用时透传原始请求的 HTTP Header，确保链路上下文（如认证 Token、TraceId）不丢失。
 *
 * @author 一陌千尘
 * @date 2026/04/28
 */
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {

    /**
     * 需要透传到下游服务的请求头白名单
     */
    private static final String[] HEADER_WHITELIST = {
            "Authorization",
            "X-Request-Id",
            "X-Real-IP",
            "X-Forwarded-For"
    };

    /**
     * 分布式链路追踪请求头（W3C Trace Context + Brave B3）
     */
    private static final String[] TRACE_HEADERS = {
            "traceparent",
            "tracestate",
            "X-B3-TraceId",
            "X-B3-SpanId",
            "X-B3-ParentSpanId",
            "X-B3-Sampled",
            "X-B3-Flags",
            "b3"
    };

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();

        // 透传白名单请求头
        for (String headerName : HEADER_WHITELIST) {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null && !headerValue.isEmpty()) {
                template.header(headerName, headerValue);
            }
        }

        // 透传分布式链路追踪请求头
        for (String headerName : TRACE_HEADERS) {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null && !headerValue.isEmpty()) {
                template.header(headerName, headerValue);
            }
        }

        // 透传所有以 X-Origin- 开头的自定义请求头
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (headerName != null && headerName.toLowerCase().startsWith("x-origin-")) {
                    String headerValue = request.getHeader(headerName);
                    if (headerValue != null && !headerValue.isEmpty()) {
                        template.header(headerName, headerValue);
                    }
                }
            }
        }
    }
}
