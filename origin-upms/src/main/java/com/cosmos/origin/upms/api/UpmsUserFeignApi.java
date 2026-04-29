package com.cosmos.origin.upms.api;

import com.cosmos.origin.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * UPMS 用户 Feign 接口
 * <p>
 * 供其他微服务调用，获取用户详情及权限信息。
 */
@FeignClient(name = "origin-upms", path = "/upms/user")
public interface UpmsUserFeignApi {

    /**
     * 根据用户名查询用户详情（含角色、部门、岗位）
     */
    @GetMapping("/detail")
    Response<?> findByUsername(@RequestParam("username") String username);
}
