package com.cosmos.origin.auth.controller;

import com.cosmos.origin.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.auth.model.vo.verificationcode.SendVerificationCodeReqVO;
import com.cosmos.origin.auth.service.VerificationCodeService;
import com.cosmos.origin.common.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "验证码模块")
@ConditionalOnProperty(prefix = "origin.module", name = "auth", havingValue = "true", matchIfMissing = true)
public class VerificationCodeController {

    @Resource
    private VerificationCodeService verificationCodeService;

    @PostMapping("/verification/code/send")
    @Operation(summary = "发送短信验证码")
    @ApiOperationLog(description = "发送短信验证码")
    public Response<?> send(@Validated @RequestBody SendVerificationCodeReqVO sendVerificationCodeReqVO) {
        return verificationCodeService.send(sendVerificationCodeReqVO);
    }
}

