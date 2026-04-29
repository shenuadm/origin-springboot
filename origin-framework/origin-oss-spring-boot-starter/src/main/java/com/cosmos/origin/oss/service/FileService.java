package com.cosmos.origin.oss.service;

import com.cosmos.origin.common.response.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 * <p>
 * 提供文件上传等通用文件操作能力
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 上传文件的访问链接
     */
    Response<?> uploadFile(MultipartFile file);
}
