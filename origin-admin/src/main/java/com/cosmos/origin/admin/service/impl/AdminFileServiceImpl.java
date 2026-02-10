package com.cosmos.origin.admin.service.impl;

import com.cosmos.origin.admin.service.AdminFileService;
import com.cosmos.origin.common.enums.ResponseCodeEnum;
import com.cosmos.origin.common.exception.BizException;
import com.cosmos.origin.common.utils.Response;
import com.cosmos.origin.oss.model.vo.UploadFileRspVO;
import com.cosmos.origin.oss.utils.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminFileServiceImpl implements AdminFileService {

    private final MinioUtil minioUtil;

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 上传文件的访问链接
     */
    @Override
    public Response<?> uploadFile(MultipartFile file) {
        try {
            // 上传文件
            String url = minioUtil.uploadFile(file);

            // 构建成功返参，将图片的访问链接返回
            return Response.success(UploadFileRspVO.builder().url(url).build());
        } catch (Exception e) {
            log.error("==> 上传文件至 Minio 错误: ", e);
            // 手动抛出业务异常，提示 “文件上传失败”
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }
}
