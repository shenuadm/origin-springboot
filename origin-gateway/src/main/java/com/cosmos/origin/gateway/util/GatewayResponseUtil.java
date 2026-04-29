package com.cosmos.origin.gateway.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gateway WebFlux 响应工具类
 *
 * @author 一陌千尘
 * @date 2026/04/28
 */
@Slf4j
public class GatewayResponseUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 写入 JSON 错误响应
     *
     * @param response   ServerHttpResponse
     * @param httpStatus HTTP 状态码
     * @param errorCode  错误码
     * @param message    错误信息
     * @return Mono<Void>
     */
    public static Mono<Void> writeErrorResponse(ServerHttpResponse response, HttpStatus httpStatus, String errorCode, String message) {
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("errorCode", errorCode);
        result.put("message", message);

        byte[] bytes;
        try {
            bytes = OBJECT_MAPPER.writeValueAsBytes(result);
        } catch (JsonProcessingException e) {
            bytes = ("{\"success\":false,\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 路径是否匹配白名单
     *
     * @param whiteList 白名单列表
     * @param path      请求路径
     * @return 是否匹配
     */
    public static boolean isPathMatch(List<String> whiteList, String path) {
        if (whiteList == null || whiteList.isEmpty()) {
            return false;
        }
        for (String pattern : whiteList) {
            if (pathMatch(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private static boolean pathMatch(String pattern, String path) {
        if (pattern.equals(path)) {
            return true;
        }
        // 支持 /** 后缀通配
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }
        // 支持 /* 单级通配
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            if (!path.startsWith(prefix)) {
                return false;
            }
            String suffix = path.substring(prefix.length());
            return !suffix.contains("/");
        }
        return false;
    }
}
