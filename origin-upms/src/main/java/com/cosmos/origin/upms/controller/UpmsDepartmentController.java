package com.cosmos.origin.upms.controller;

import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.upms.domain.dos.UpmsDepartmentDO;
import com.cosmos.origin.upms.service.UpmsDepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UPMS 部门管理控制器
 */
@RestController
@RequestMapping("/upms/department")
@RequiredArgsConstructor
@Tag(name = "UPMS 部门管理")
public class UpmsDepartmentController {

    private final UpmsDepartmentService departmentService;

    @GetMapping("/tree")
    @Operation(summary = "部门树")
    @ApiOperationLog(description = "查询部门树")
    public Response<List<UpmsDepartmentDO>> tree() {
        return departmentService.tree();
    }

    @PostMapping("/add")
    @Operation(summary = "添加部门")
    @ApiOperationLog(description = "添加部门")
    public Response<?> add(@RequestBody UpmsDepartmentDO department) {
        return departmentService.add(department);
    }

    @PutMapping("/update")
    @Operation(summary = "更新部门")
    @ApiOperationLog(description = "更新部门")
    public Response<?> update(@RequestBody UpmsDepartmentDO department) {
        return departmentService.update(department);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除部门")
    @ApiOperationLog(description = "删除部门")
    public Response<?> delete(@PathVariable Long id) {
        return departmentService.delete(id);
    }
}
