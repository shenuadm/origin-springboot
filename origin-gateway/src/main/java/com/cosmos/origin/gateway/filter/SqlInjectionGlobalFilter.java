package com.cosmos.origin.gateway.filter;

import com.cosmos.origin.gateway.config.GatewayProperties;
import com.cosmos.origin.gateway.util.GatewayResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Gateway SQL 注入拦截过滤器
 * <p>
 * 对 GET 请求参数和 POST/PUT JSON/表单数据进行 SQL 注入关键字检测。
 *
 * @author 一陌千尘
 * @date 2026/04/28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SqlInjectionGlobalFilter implements GlobalFilter, Ordered {

    private final GatewayProperties gatewayProperties;

    /**
     * SQL 注入关键字黑名单
     */
    private static final Set<String> SQL_KEYWORDS = new HashSet<>(Arrays.asList(
            "select", "insert", "update", "delete", "drop", "truncate",
            "create", "alter", "exec", "execute", "union", "into",
            "load_file", "outfile", "dumpfile", "script", "alert",
            "--", "/*", "*/", ";", "'", "\"", "||", "&&"
    ));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!gatewayProperties.isSqlInjectionEnabled()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();
        String contentType = request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        URI uri = request.getURI();

        // GET 请求：检查 Query 参数
        if (method == HttpMethod.GET) {
            String rawQuery = uri.getRawQuery();
            if (rawQuery != null && !rawQuery.isEmpty() && containsSqlInjection(rawQuery)) {
                log.warn("[Gateway] SQL 注入风险（GET 参数）：path={}, query={}", uri.getPath(), rawQuery);
                return sqlInjectionResponse(exchange);
            }
            return chain.filter(exchange);
        }

        // POST/PUT 请求：检查 Body
        if ((method == HttpMethod.POST || method == HttpMethod.PUT)
                && isTextContentType(contentType)) {
            String bodyString = resolveBodyFromRequest(request);
            if (bodyString != null && !bodyString.isEmpty() && containsSqlInjection(bodyString)) {
                log.warn("[Gateway] SQL 注入风险（POST/PUT Body）：path={}", uri.getPath());
                return sqlInjectionResponse(exchange);
            }

            // 重新封装 Body（因为已经消费过一次）
            if (bodyString != null) {
                byte[] newBytes = bodyString.getBytes(StandardCharsets.UTF_8);
                DataBuffer bodyDataBuffer = exchange.getResponse().bufferFactory().wrap(newBytes);
                Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);

                HttpHeaders headers = new HttpHeaders();
                headers.putAll(request.getHeaders());
                headers.remove(HttpHeaders.CONTENT_LENGTH);
                headers.setContentLength(newBytes.length);

                ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(request) {
                    @Override
                    public HttpHeaders getHeaders() {
                        return headers;
                    }

                    @Override
                    public Flux<DataBuffer> getBody() {
                        return bodyFlux;
                    }
                };

                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            }
        }

        return chain.filter(exchange);
    }

    /**
     * 检测字符串是否包含 SQL 注入关键字
     */
    private boolean containsSqlInjection(String input) {
        String lower = input.toLowerCase();
        for (String keyword : SQL_KEYWORDS) {
            if (lower.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从请求体中读取字符串
     */
    private String resolveBodyFromRequest(ServerHttpRequest request) {
        Flux<DataBuffer> body = request.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            byte[] content = new byte[buffer.readableByteCount()];
            buffer.read(content);
            DataBufferUtils.release(buffer);
            bodyRef.set(new String(content, StandardCharsets.UTF_8));
        });
        return bodyRef.get();
    }

    private boolean isTextContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.contains(MediaType.APPLICATION_JSON_VALUE)
                || contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    private Mono<Void> sqlInjectionResponse(ServerWebExchange exchange) {
        return GatewayResponseUtil.writeErrorResponse(
                exchange.getResponse(),
                HttpStatus.FORBIDDEN,
                "20004",
                "请求包含非法字符，已被拦截"
        );
    }

    @Override
    public int getOrder() {
        // 在认证过滤器之后执行
        return -50;
    }
}
