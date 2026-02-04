package com.cosmos.origin.jwt.utils;

import com.cosmos.origin.common.utils.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 响应参数工具类
 *
 * @author 一陌千尘
 * @date 2025/11/04
 */
public class ResultUtil {

    /**
     * 成功响参
     *
     * @param response 响应对象
     * @param result   响应参数
     * @throws IOException 异常
     */
    public static void ok(HttpServletResponse response, Response<?> result) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();

        ObjectMapper mapper = new ObjectMapper();
        writer.write(mapper.writeValueAsString(result));
        writer.flush();
        writer.close();
    }

    /**
     * 失败响参
     *
     * @param response 响应对象
     * @param result   响应参数
     * @throws IOException 异常
     */
    public static void fail(HttpServletResponse response, Response<?> result) throws IOException {
        ok(response, result);
    }

    /**
     * 失败响参
     *
     * @param response 响应对象
     * @param status   可指定响应码，如 401 等
     * @param result   响应参数
     * @throws IOException 异常
     */
    public static void fail(HttpServletResponse response, int status, Response<?> result) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();

        ObjectMapper mapper = new ObjectMapper();
        writer.write(mapper.writeValueAsString(result));
        writer.flush();
        writer.close();
    }
}
