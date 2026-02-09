package com.cosmos.origin.admin.service;

import com.cosmos.origin.admin.domain.dos.LoginLogDO;
import com.cosmos.origin.admin.domain.mapper.LoginLogMapper;
import com.cosmos.origin.admin.enums.LoginStatusEnum;
import com.cosmos.origin.admin.utils.IpLocationUtil;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 登录日志服务
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogService {

    private final LoginLogMapper loginLogMapper;

    /**
     * 记录登录日志
     *
     * @param username 用户名
     * @param status   登录状态
     * @param message  提示消息
     * @param request  HTTP请求
     */
    public void recordLoginLog(String username, LoginStatusEnum status, String message, HttpServletRequest request) {
        try {
            String userAgentStr = request.getHeader("User-Agent");
            String ip = getClientIp(request);

            // 解析UserAgent
            String browser = "Unknown";
            String os = "Unknown";
            if (userAgentStr != null) {
                UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
                Browser b = userAgent.getBrowser();
                OperatingSystem o = userAgent.getOperatingSystem();
                browser = b.getName();
                os = o.getName();
            }

            LoginLogDO loginLog = LoginLogDO.builder()
                    .username(username)
                    .ipAddress(ip)
                    .loginLocation(parseLocation(ip))
                    .browser(browser)
                    .os(os)
                    .status(status.getCode())
                    .message(message)
                    .userAgent(userAgentStr)
                    .build();

            loginLogMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 解析 IP 地址位置
     * <p>
     * 使用 ip2region 离线 IP 地址库解析 IP 归属地
     *
     * @param ip IP 地址
     * @return IP 归属地
     */
    private String parseLocation(String ip) {
        return IpLocationUtil.getLocation(ip);
    }
}
