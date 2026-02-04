package com.cosmos.origin.common.utils;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Schema(name = "分页响应参数工具类")
@Data
public class PageResponse<T> extends Response<List<T>> {

    @Schema(description = "总记录数，默认为 0")
    private long total = 0L;

    @Schema(description = "每页显示的记录数，默认每页显示 10 条")
    private long size = 10L;

    @Schema(description = "当前页码，默认为 1")
    private long current = 1L;

    @Schema(description = "总页数")
    private long pages;

    /**
     * 成功响应
     *
     * @param page Mybatis Flex 提供的分页接口
     * @param data 数据
     * @param <T>  数据类型
     * @return 响应
     */
    public static <T> PageResponse<T> success(Page page, List<T> data) {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(true);
        response.setCurrent(Objects.isNull(page) ? 1L : page.getPageNumber());
        response.setSize(Objects.isNull(page) ? 10L : page.getPageSize());
        response.setPages(Objects.isNull(page) ? 0L : page.getTotalPage());
        response.setTotal(Objects.isNull(page) ? 0L : page.getTotalRow());
        response.setData(data);
        return response;
    }
}
