package com.cosmos.origin.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件大小校验器
 *
 * @author 一陌千尘
 * @date 2025/11/06
 */
public class FileSizeValidator implements ConstraintValidator<FileSize, MultipartFile> {

    private long maxSize;
    private long minSize;

    @Override
    public void initialize(FileSize constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.minSize = constraintAnnotation.minSize();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // 空文件由其他验证器处理
        }

        long fileSize = file.getSize();

        if (fileSize < minSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("文件大小不能小于 %d 字节", minSize)
            ).addConstraintViolation();
            return false;
        }

        if (fileSize > maxSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("文件大小不能超过 %d 字节 (%.2f MB)", maxSize, maxSize / 1024.0 / 1024.0)
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
