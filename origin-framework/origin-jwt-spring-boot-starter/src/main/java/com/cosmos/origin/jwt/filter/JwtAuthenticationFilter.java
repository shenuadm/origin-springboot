package com.cosmos.origin.jwt.filter;

import com.cosmos.origin.jwt.constant.JwtSecurityConstants;
import com.cosmos.origin.jwt.exception.UsernameOrPasswordNullException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
     * 指定登录URL的构造器
     *
     * @param loginProcessingUrl 登录处理URL
     */
    public JwtAuthenticationFilter(String loginProcessingUrl) {
        super(createLoginMatcher(loginProcessingUrl));
    }

    /**
     * 账号锁定检查函数
     */
    @Setter
    private Function<String, Void> lockCheckFunction;

    /**
     * 创建登录请求匹配器
     *
     * @param loginProcessingUrl 登录处理URL
     * @return {@link RequestMatcher } 登录请求匹配器
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

    /**
     * 尝试进行身份验证
     *
     * @param request  登录请求
     * @param response 登录响应
     * @return {@link Authentication } 身份验证对象
     * @throws AuthenticationException 身份验证异常
     * @throws IOException             输入输出异常
     */
    @Override
    public Authentication attemptAuthentication(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) throws AuthenticationException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        // 解析提交的 JSON 数据
        JsonNode jsonNode = mapper.readTree(request.getInputStream());

        // 使用自定义参数名获取用户名和密码
        String usernameParameter = JwtSecurityConstants.USERNAME_PARAMETER;
        JsonNode usernameNode = jsonNode.get(usernameParameter);
        String passwordParameter = JwtSecurityConstants.PASSWORD_PARAMETER;
        JsonNode passwordNode = jsonNode.get(passwordParameter);

        // 判断用户名、密码是否为空
        if (Objects.isNull(usernameNode) || Objects.isNull(passwordNode)
                || StringUtils.isBlank(usernameNode.textValue()) || StringUtils.isBlank(passwordNode.textValue())) {
            throw new UsernameOrPasswordNullException("用户名或密码不能为空");
        }

        String username = usernameNode.textValue();
        String password = passwordNode.textValue();

        // 将用户名保存到请求属性中，供失败处理器使用
        request.setAttribute(JwtSecurityConstants.LOGIN_USERNAME_ATTRIBUTE, username);

        // 获取记住我参数并保存到请求属性中
        JsonNode rememberMeNode = jsonNode.get(JwtSecurityConstants.REMEMBER_ME_PARAMETER);
        boolean rememberMe = rememberMeNode != null && rememberMeNode.asBoolean(false);
        request.setAttribute(JwtSecurityConstants.REMEMBER_ME_ATTRIBUTE, rememberMe);

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
