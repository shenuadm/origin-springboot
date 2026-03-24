package com.cosmos.origin.common;

import com.cosmos.origin.common.enums.ResponseCodeEnum;
import com.cosmos.origin.common.exception.BizException;
import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通用响应工具类测试
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
public class ResponseTests {

    @Test
    void testSuccessResponseWithoutData() {
        Response<String> response = Response.success();

        assertTrue(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getErrorCode());
        assertNull(response.getData());
    }

    @Test
    void testSuccessResponseWithData() {
        String data = "test data";
        Response<String> response = Response.success(data);

        assertTrue(response.isSuccess());
        assertEquals(data, response.getData());
        assertNull(response.getMessage());
        assertNull(response.getErrorCode());
    }

    @Test
    void testFailResponseWithoutMessage() {
        Response<String> response = Response.fail();

        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getErrorCode());
    }

    @Test
    void testFailResponseWithMessage() {
        String errorMessage = "Error occurred";
        Response<String> response = Response.fail(errorMessage);

        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getErrorCode());
    }

    @Test
    void testFailResponseWithCodeAndMessage() {
        String errorCode = "10001";
        String errorMessage = "Parameter error";
        Response<String> response = Response.fail(errorCode, errorMessage);

        assertFalse(response.isSuccess());
        assertEquals(errorCode, response.getErrorCode());
        assertEquals(errorMessage, response.getMessage());
    }

    @Test
    void testFailResponseWithBizException() {
        BizException exception = new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        Response<String> response = Response.fail(exception);

        assertFalse(response.isSuccess());
        assertEquals(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), response.getErrorCode());
        assertEquals(ResponseCodeEnum.PARAM_NOT_VALID.getErrorMessage(), response.getMessage());
    }

    @Test
    void testFailResponseWithBaseExceptionInterface() {
        Response<String> response = Response.fail(ResponseCodeEnum.UNAUTHORIZED);

        assertFalse(response.isSuccess());
        assertEquals(ResponseCodeEnum.UNAUTHORIZED.getErrorCode(), response.getErrorCode());
        assertEquals(ResponseCodeEnum.UNAUTHORIZED.getErrorMessage(), response.getMessage());
    }

    @Test
    void testPageResponse() {
        List<String> data = new ArrayList<>();
        data.add("item1");
        data.add("item2");

        // 使用 MyBatis Flex 的 Page 对象
        com.mybatisflex.core.paginate.Page<String> page = new com.mybatisflex.core.paginate.Page<>(1, 10);
        page.setTotalRow(2);
        page.setTotalPage(1);

        PageResponse<String> response = PageResponse.success(page, data);

        assertTrue(response.isSuccess());
        assertEquals(2, response.getTotal());
        assertEquals(1, response.getCurrent());
        assertEquals(10, response.getSize());
        assertEquals(2, response.getData().size());
    }

    @Test
    void testPageResponseWithEmptyData() {
        List<String> data = new ArrayList<>();

        com.mybatisflex.core.paginate.Page<String> page = new com.mybatisflex.core.paginate.Page<>(1, 10);
        page.setTotalRow(0);
        page.setTotalPage(0);

        PageResponse<String> response = PageResponse.success(page, data);

        assertTrue(response.isSuccess());
        assertEquals(0, response.getTotal());
        assertEquals(1, response.getCurrent());
        assertEquals(10, response.getSize());
        assertTrue(response.getData().isEmpty());
    }
}
