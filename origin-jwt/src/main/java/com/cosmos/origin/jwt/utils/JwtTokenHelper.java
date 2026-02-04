package com.cosmos.origin.jwt.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

/**
 * JWT 工具类
 *
 * @author 一陌千尘
 * @date 2025/11/04
 */
@Slf4j
@Component
public class JwtTokenHelper implements InitializingBean {

    /**
     * 签发人
     */
    @Value("${jwt.issuer}")
    private String issuer;
    /**
     * 秘钥
     */
    private Key key;

    /**
     * JWT 解析
     */
    private JwtParser jwtParser;
    /**
     * Token 失效时间（分钟）
     */
    @Value("${jwt.tokenExpireTime}")
    private Long tokenExpireTime;

    /**
     * 生成一个 Base64 的安全秘钥
     *
     * @return Base64 的安全秘钥
     */
    private static String generateBase64Key() {
        // 生成安全秘钥
        Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        // 将密钥进行 Base64 编码
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static void main(String[] args) {
        String key = generateBase64Key();
        log.info("key: {}", key);
    }

    /**
     * 解码配置文件中配置的 Base 64 编码 key 为秘钥
     *
     * @param base64Key Base 64 编码 key
     */
    @Value("${jwt.secret}")
    public void setBase64Key(String base64Key) {
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
    }

    /**
     * 初始化 JwtParser
     */
    @Override
    public void afterPropertiesSet() {
        // 考虑到不同服务器之间可能存在时钟偏移，setAllowedClockSkewSeconds 用于设置能够容忍的最大的时钟误差
        jwtParser = Jwts.parserBuilder().requireIssuer(issuer)
                .setSigningKey(key).setAllowedClockSkewSeconds(10)
                .build();
    }

    /**
     * 生成 Token
     *
     * @param username 用户名
     * @return Token
     */
    public String generateToken(String username) {
        LocalDateTime now = LocalDateTime.now();
        // 设置 Token 失效时间
        LocalDateTime expireTime = now.plusMinutes(tokenExpireTime);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expireTime.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 Token
     *
     * @param token Token
     * @return 解析后的 Token
     */
    public Jws<Claims> parseToken(String token) {
        try {
            return jwtParser.parseClaimsJws(token);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("Token 不可用", e);
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("Token 失效", e);
        }
    }

    /**
     * 校验 Token 是否可用
     *
     * @param token Token
     * @return 校验结果
     */
    public void validateToken(String token) {
        jwtParser.parseClaimsJws(token);
    }

    /**
     * 解析 Token 获取用户名
     *
     * @param token Token
     * @return 用户名
     */
    public String getUsernameByToken(String token) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            return username;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
