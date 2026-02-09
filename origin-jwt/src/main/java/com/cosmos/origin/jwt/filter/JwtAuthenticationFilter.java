package com.cosmos.origin.jwt.filter;

import com.cosmos.origin.jwt.exception.UsernameOrPasswordNullException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

/**
 * 自定义登录过滤器
 * <p>
 * 支持自定义登录URL和请求参数名
 *
 * @author 一陌千尘
 * @date 2025/11/04
 */
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * 登录URL，默认 /login
     */
    @Getter
    @Setter
    private String loginProcessingUrl;

    /**
     * 用户名字段名，默认 username
     */
    @Getter
    @Setter
    private String usernameParameter = "username";

    /**
     * 密码字段名，默认 password
     */
    @Getter
    @Setter
    private String passwordParameter = "password";

    /**
     * 账号锁定检查函数（在密码验证前执行）
     */
    @Setter
    private Function<String, Void> lockCheckFunction;

    /**
     * 指定登录URL的构造器
     *
     * @param loginProcessingUrl 登录处理URL
     */
    public JwtAuthenticationFilter(String loginProcessingUrl) {
        super(createLoginMatcher(loginProcessingUrl));
        this.loginProcessingUrl = loginProcessingUrl;
    }

    /**
     * 创建登录请求匹配器
     */
    private static RequestMatcher createLoginMatcher(String loginProcessingUrl) {
        return new RequestMatcher() {
            @Override
            public boolean matches(HttpServletRequest request) {
                return HttpMethod.POST.name().equalsIgnoreCase(request.getMethod()) &&
                        loginProcessingUrl.equals(request.getServletPath());
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
    public Authentication attemptAuthentication(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) throws AuthenticationException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        // 解析提交的 JSON 数据
        JsonNode jsonNode = mapper.readTree(request.getInputStream());

        // 使用自定义参数名获取用户名和密码
        JsonNode usernameNode = jsonNode.get(usernameParameter);
        JsonNode passwordNode = jsonNode.get(passwordParameter);

        // 判断用户名、密码是否为空
        if (Objects.isNull(usernameNode) || Objects.isNull(passwordNode)
                || StringUtils.isBlank(usernameNode.textValue()) || StringUtils.isBlank(passwordNode.textValue())) {
            throw new UsernameOrPasswordNullException("用户名或密码不能为空");
        }

        String username = usernameNode.textValue();
        String password = passwordNode.textValue();

        // 将用户名保存到请求属性中，供失败处理器使用
        request.setAttribute("LOGIN_USERNAME", username);

        // 在密码验证前检查账号是否被锁定
        if (lockCheckFunction != null) {
            lockCheckFunction.apply(username);
        }

        // 将用户名、密码封装到 Token 中
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(username, password);
        return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    }
}
