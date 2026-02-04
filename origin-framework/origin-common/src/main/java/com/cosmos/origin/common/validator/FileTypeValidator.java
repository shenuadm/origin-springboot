package com.cosmos.origin.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 文件类型校验器
 *
 * @author 一陌千尘
 * @date 2025/11/06
 */
public class FileTypeValidator implements ConstraintValidator<FileType, MultipartFile> {

    private List<String> allowedTypes;

    // 预定义的文件类型
    private static final List<String> IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");
    private static final List<String> VIDEO_TYPES = Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "mkv");
    private static final List<String> DOCUMENT_TYPES = Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx");

    @Override
    public void initialize(FileType constraintAnnotation) {
        this.allowedTypes = Arrays.asList(constraintAnnotation.allowedTypes());
        String category = constraintAnnotation.category();

        // 如果没有指定具体类型，根据分类设置默认类型
        if (this.allowedTypes.isEmpty() && !category.isEmpty()) {
            switch (category.toUpperCase()) {
                case "IMAGE":
                    this.allowedTypes = IMAGE_TYPES;
                    break;
                case "VIDEO":
                    this.allowedTypes = VIDEO_TYPES;
                    break;
                case "DOCUMENT":
                    this.allowedTypes = DOCUMENT_TYPES;
                    break;
            }
        }
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // 空文件由其他验证器处理
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        // 获取文件扩展名
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex + 1).toLowerCase();
        }

        boolean isValid = allowedTypes.contains(extension);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("文件类型 '%s' 不支持，支持的类型：%s", extension, String.join(", ", allowedTypes))
            ).addConstraintViolation();
        }

        return isValid;
    }
}
