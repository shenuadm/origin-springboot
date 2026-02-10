package com.cosmos.origin.admin.controller;

import com.cosmos.origin.admin.service.AdminFileService;
import com.cosmos.origin.biz.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin 文件模块")
public class AdminFileController {

    private final AdminFileService fileService;

    @PostMapping("/file/upload")
    @Operation(summary = "文件上传")
    @ApiOperationLog(description = "文件上传")
    public Response<?> uploadFile(@RequestParam MultipartFile file) {
        return fileService.uploadFile(file);
    }
}
