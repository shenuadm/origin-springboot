package com.cosmos.origin.upms.controller;

import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.upms.domain.dos.UpmsPositionDO;
import com.cosmos.origin.upms.service.UpmsPositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * UPMS 岗位管理控制器
 */
@RestController
@RequestMapping("/upms/position")
@RequiredArgsConstructor
@Tag(name = "UPMS 岗位管理")
public class UpmsPositionController {

    private final UpmsPositionService positionService;

    @PostMapping("/page")
    @Operation(summary = "岗位分页列表")
    @ApiOperationLog(description = "查询岗位分页列表")
    public PageResponse<?> page(@RequestParam(defaultValue = "1") Long current,
                                 @RequestParam(defaultValue = "10") Long size) {
        return positionService.page(current, size);
    }

    @PostMapping("/add")
    @Operation(summary = "添加岗位")
    @ApiOperationLog(description = "添加岗位")
    public Response<?> add(@RequestBody UpmsPositionDO position) {
        return positionService.add(position);
    }

    @PutMapping("/update")
    @Operation(summary = "更新岗位")
    @ApiOperationLog(description = "更新岗位")
    public Response<?> update(@RequestBody UpmsPositionDO position) {
        return positionService.update(position);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除岗位")
    @ApiOperationLog(description = "删除岗位")
    public Response<?> delete(@PathVariable Long id) {
        return positionService.delete(id);
    }
}
