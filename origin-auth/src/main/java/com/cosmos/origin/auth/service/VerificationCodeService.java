package com.cosmos.origin.auth.service;

import com.cosmos.origin.auth.model.vo.verificationcode.SendVerificationCodeReqVO;
import com.cosmos.origin.common.response.Response;

public interface VerificationCodeService {

    /**
     * 发送短信验证码
     *
     * @param sendVerificationCodeReqVO 发送短信验证码请求参数
     * @return 发送结果
     */
    Response<?> send(SendVerificationCodeReqVO sendVerificationCodeReqVO);
}
