package com.imapcloud.nest.v2.common.exception;

import com.geoai.common.core.exception.BizException;

/**
 * 文音频转码异常<br/>
 * 详细定义见：{@link UosServiceErrorCode#AUDIO_TRANSCODE_ERROR}
 * @author Vastfy
 * @date 2023/02/23 10:30
 * @since 2.2.3
 */
public class AudioTranscodeException extends BizException {

    @Override
    public String getId() {
        // CODE: 10322
        return UosServiceErrorCode.AUDIO_TRANSCODE_ERROR.toBizErrorCode();
    }

    public AudioTranscodeException() {
    }

    public AudioTranscodeException(String message) {
        super(message);
    }

    public AudioTranscodeException(String message, Throwable cause) {
        super(message, cause);
    }

}
