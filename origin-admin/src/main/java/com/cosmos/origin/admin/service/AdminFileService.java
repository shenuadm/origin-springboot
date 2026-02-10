package com.cosmos.origin.admin.service;

import com.cosmos.origin.common.utils.Response;
import org.springframework.web.multipart.MultipartFile;

public interface AdminFileService {

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 上传文件的访问链接
     */
    Response<?> uploadFile(MultipartFile file);
}

