package com.cosmos.origin.jwt.filter;

import com.cosmos.origin.jwt.exception.UsernameOrPasswordNullException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

/**
 * 自定义登录过滤器
 *
 * @author 一陌千尘
 * @date 2025/11/04
 */
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * 指定用户登录的访问地址
     */
    public JwtAuthenticationFilter() {
        super(createLoginMatcher());
    }

    private static RequestMatcher createLoginMatcher() {
        return new RequestMatcher() {
            @Override
            public boolean matches(HttpServletRequest request) {
                return HttpMethod.POST.name().equalsIgnoreCase(request.getMethod()) &&
                        "/login".equals(request.getServletPath());
            }

            @Override
            public MatchResult matcher(HttpServletRequest request) {
                if (matches(request)) {
                    return MatchResult.match(Collections.emptyMap());
                }
                return MatchResult.notMatch();
            }
        };
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();
        // 解析提交的 JSON 数据
        JsonNode jsonNode = mapper.readTree(request.getInputStream());
        JsonNode usernameNode = jsonNode.get("username");
        JsonNode passwordNode = jsonNode.get("password");

        // 判断用户名、密码是否为空
        if (Objects.isNull(usernameNode) || Objects.isNull(passwordNode)
                || StringUtils.isBlank(usernameNode.textValue()) || StringUtils.isBlank(passwordNode.textValue())) {
            throw new UsernameOrPasswordNullException("用户名或密码不能为空");
        }

        String username = usernameNode.textValue();
        String password = passwordNode.textValue();

        // 将用户名、密码封装到 Token 中
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(username, password);
        return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    }
}
