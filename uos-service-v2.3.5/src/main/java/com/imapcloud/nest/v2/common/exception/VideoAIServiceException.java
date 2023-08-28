package com.imapcloud.nest.v2.common.exception;

import com.geoai.common.core.exception.BizException;

/**
 * 视频AI识别服务异常<br/>
 * 详细定义见：{@link UosServiceErrorCode#VIDEO_AI_SERVICE_ERROR}
 * @author Vastfy
 * @date 2022/12/30 17:30
 * @since 2.1.7
 */
public class VideoAIServiceException extends BizException {

    @Override
    public String getId() {
        // CODE: 10311
        return UosServiceErrorCode.VIDEO_AI_SERVICE_ERROR.toBizErrorCode();
    }

    public VideoAIServiceException() {
    }

    public VideoAIServiceException(String message) {
        super(message);
    }

    public VideoAIServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
