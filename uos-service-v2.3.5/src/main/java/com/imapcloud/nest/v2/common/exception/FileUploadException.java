package com.imapcloud.nest.v2.common.exception;

import com.geoai.common.core.exception.BizException;

/**
 * 文件上传异常<br/>
 * 详细定义见：{@link UosServiceErrorCode#FILE_UPLOAD_ERROR}
 * @author Vastfy
 * @date 2023/02/23 10:30
 * @since 2.2.3
 */
public class FileUploadException extends BizException {

    @Override
    public String getId() {
        // CODE: 10321
        return UosServiceErrorCode.FILE_UPLOAD_ERROR.toBizErrorCode();
    }

    public FileUploadException() {
    }

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }

}
