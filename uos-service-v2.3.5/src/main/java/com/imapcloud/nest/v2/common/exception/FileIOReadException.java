package com.imapcloud.nest.v2.common.exception;

import com.geoai.common.core.exception.BizException;

/**
 * 文件IO读取异常<br/>
 * 详细定义见：{@link UosServiceErrorCode#FILE_IO_READ_ERROR}
 * @author Vastfy
 * @date 2023/02/23 10:30
 * @since 2.2.3
 */
public class FileIOReadException extends BizException {

    @Override
    public String getId() {
        // CODE: 10320
        return UosServiceErrorCode.FILE_IO_READ_ERROR.toBizErrorCode();
    }

    public FileIOReadException() {
    }

    public FileIOReadException(String message) {
        super(message);
    }

    public FileIOReadException(String message, Throwable cause) {
        super(message, cause);
    }

}
