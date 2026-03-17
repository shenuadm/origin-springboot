package com.cosmos.origin.common;

import com.cosmos.origin.common.enums.ResponseCodeEnum;
import com.cosmos.origin.common.exception.BaseExceptionInterface;
import com.cosmos.origin.common.exception.BizException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 异常类测试
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
public class ExceptionTests {

    @Test
    void testBizExceptionWithEnum() {
        BizException exception = new BizException(ResponseCodeEnum.SYSTEM_ERROR);

        assertEquals(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals(ResponseCodeEnum.SYSTEM_ERROR.getErrorMessage(), exception.getErrorMessage());
    }

    @Test
    void testBizExceptionWithCustomEnum() {
        BizException exception = new BizException(ResponseCodeEnum.PARAM_NOT_VALID);

        assertEquals("10001", exception.getErrorCode());
        assertEquals("参数错误", exception.getErrorMessage());
    }

    @Test
    void testBizExceptionInheritance() {
        BizException exception = new BizException(ResponseCodeEnum.UNAUTHORIZED);

        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    void testBaseExceptionInterface() {
        BaseExceptionInterface exceptionInterface = ResponseCodeEnum.FORBIDDEN;

        assertNotNull(exceptionInterface.getErrorCode());
        assertNotNull(exceptionInterface.getErrorMessage());
        assertFalse(exceptionInterface.getErrorCode().isEmpty());
        assertFalse(exceptionInterface.getErrorMessage().isEmpty());
    }

    @Test
    void testAllResponseCodeEnums() {
        ResponseCodeEnum[] codes = ResponseCodeEnum.values();

        assertTrue(codes.length > 0);

        for (ResponseCodeEnum code : codes) {
            assertNotNull(code.getErrorCode());
            assertNotNull(code.getErrorMessage());
            assertFalse(code.getErrorCode().isEmpty());
            assertFalse(code.getErrorMessage().isEmpty());
            assertTrue(code.getErrorCode().matches("\\d+")); // 应该是数字
        }
    }

    @Test
    void testCommonErrorCodes() {
        assertEquals("10000", ResponseCodeEnum.SYSTEM_ERROR.getErrorCode());
        assertEquals("10001", ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode());
        assertEquals("20000", ResponseCodeEnum.LOGIN_FAIL.getErrorCode());
        assertEquals("20002", ResponseCodeEnum.UNAUTHORIZED.getErrorCode());
        assertEquals("20004", ResponseCodeEnum.FORBIDDEN.getErrorCode());
    }
}
