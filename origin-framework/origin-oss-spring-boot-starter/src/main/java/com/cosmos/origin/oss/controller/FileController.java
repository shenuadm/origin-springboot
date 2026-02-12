package com.cosmos.origin.oss.controller;

import com.cosmos.origin.common.utils.Response;
import com.cosmos.origin.oss.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 * 提供通用的文件上传接口
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
@Tag(name = "文件模块")
public class FileController {

    private final FileService fileService;

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 上传文件的访问链接
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public Response<?> uploadFile(@RequestParam MultipartFile file) {
        return fileService.uploadFile(file);
    }
}
