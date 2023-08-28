package com.imapcloud.nest.v2.common.exception;

import com.geoai.common.core.exception.BizException;

/**
 * 视频AI识别路数超限异常<br/>
 * 详细定义见：{@link UosServiceErrorCode#VIDEO_AI_CHANNEL_EXCEED}
 * @author Vastfy
 * @date 2022/12/30 17:30
 * @since 2.1.7
 */
public class VideoAIChannelExceedException extends BizException {

    @Override
    public String getId() {
        // CODE: 10310
        return UosServiceErrorCode.VIDEO_AI_CHANNEL_EXCEED.toBizErrorCode();
    }

    public VideoAIChannelExceedException() {
    }

    public VideoAIChannelExceedException(String message) {
        super(message);
    }

    public VideoAIChannelExceedException(String message, Throwable cause) {
        super(message, cause);
    }

}
