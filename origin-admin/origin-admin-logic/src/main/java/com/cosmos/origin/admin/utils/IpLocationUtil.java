package com.cosmos.origin.admin.utils;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

/**
 * IP 地址解析工具类
 * <p>
 * 使用 ip2region 离线 IP 地址库解析 IP 归属地
 *
 * @author 一陌千尘
 * @date 2025/02/09
 */
@Slf4j
public class IpLocationUtil {

    private static Searcher searcher;
    private static final String DB_PATH = "ip2region/ip2region.xdb";

    static {
        try {
            // 从 classpath 加载 xdb 文件到内存
            ClassPathResource resource = new ClassPathResource(DB_PATH);
            InputStream inputStream = resource.getInputStream();
            // 加载为 LongByteArray
            LongByteArray dbContent = Searcher.loadContentFromInputStream(inputStream);

            // 使用完全基于内存的查询算法 
            searcher = Searcher.newWithBuffer(Version.IPv4, dbContent);
            log.info("ip2region 数据库加载成功，数据库路径: {}", DB_PATH);
        } catch (Exception e) {
            log.error("ip2region 数据库加载失败", e);
        }
    }

    /**
     * 根据 IP 地址解析归属地
     * <p>
     * 返回格式: 国家|区域|省份|城市|ISP
     * 例如: 中国|0|广东省|深圳市|电信
     *
     * @param ip IP 地址
     * @return 归属地信息
     */
    public static String getLocation(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "未知";
        }

        // 本地 IP 特殊处理
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "localhost".equalsIgnoreCase(ip)) {
            return "本地";
        }

        // 内网 IP 特殊处理
        if (isInternalIp(ip)) {
            return "内网IP";
        }

        if (searcher == null) {
            log.warn("ip2region searcher 未初始化，返回未知");
            return "未知";
        }

        try {
            String region = searcher.search(ip);
            // 格式化输出: 只保留省份和城市信息
            return formatLocation(region);
        } catch (Exception e) {
            log.error("IP 地址解析失败: {}", ip, e);
            return "未知";
        }
    }

    /**
     * 格式化地理位置信息
     * <p>
     * 原始格式: 中国|0|广东省|深圳市|电信
     * 格式化后: 中国 广东省 深圳市
     *
     * @param region 原始地理位置信息
     * @return 格式化后的地理位置
     */
    private static String formatLocation(String region) {
        if (region == null || region.isEmpty()) {
            return "未知";
        }

        String[] parts = region.split("\\|");
        StringBuilder location = new StringBuilder();

        // parts[0]: 国家
        if (parts.length > 0 && !"0".equals(parts[0])) {
            location.append(parts[0]);
        }

        // parts[2]: 省份
        if (parts.length > 2 && !"0".equals(parts[2])) {
            if (!location.isEmpty()) {
                location.append(" ");
            }
            location.append(parts[2]);
        }

        // parts[3]: 城市
        if (parts.length > 3 && !"0".equals(parts[3])) {
            if (!location.isEmpty()) {
                location.append(" ");
            }
            location.append(parts[3]);
        }

        String result = location.toString().trim();
        return result.isEmpty() ? "未知" : result;
    }

    /**
     * 判断是否为内网 IP
     *
     * @param ip IP 地址
     * @return 是否为内网 IP
     */
    private static boolean isInternalIp(String ip) {
        if (ip.startsWith("192.168.") || ip.startsWith("10.")) {
            return true;
        }
        if (ip.startsWith("172.")) {
            String[] parts = ip.split("\\.");
            if (parts.length >= 2) {
                try {
                    int secondOctet = Integer.parseInt(parts[1]);
                    if (secondOctet >= 16 && secondOctet <= 31) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        return false;
    }

    /**
     * 释放资源
     */
    public static void destroy() {
        if (searcher != null) {
            try {
                searcher.close();
                log.info("ip2region searcher 资源已释放");
            } catch (Exception e) {
                log.error("ip2region searcher 资源释放失败", e);
            }
        }
    }
}
