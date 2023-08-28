package com.imapcloud.nest.v2.common.exception;

import com.geoai.common.web.rest.IServiceErrorCode;
import lombok.Getter;

/**
 * UOS服务相关业务错误码定义<br/>
 * 示例如下；需要定义枚举类实现 {@link IServiceErrorCode} 接口
 * @author Vastfy
 * @date 2022/10/26 10:50
 * @since 2.1.4
 */
@Getter
public enum UosServiceErrorCode implements IServiceErrorCode<UosServiceErrorCode> {


    /*---------------------------------特殊相关业务错误码定义-----------------------------------------*/

    SOME_AI_REC_FUNCTION_EXPIRED("00", "部分AI识别功能授权已失效", "geoai.file.error.code.10300"),
    AI_REC_FUNCTION_UNAUTHORIZED("01", "AI识别功能未授权", "geoai.file.error.code.10301"),


    VIDEO_AI_CHANNEL_EXCEED("10", "视频AI识别路数超出限制", "geoai.file.error.code.10310"),
    VIDEO_AI_SERVICE_ERROR("11", "视频AI识别服务异常", "geoai.file.error.code.10311"),

    FILE_IO_READ_ERROR("20", "文件读取失败", "geoai.file.error.code.10320"),
    FILE_UPLOAD_ERROR("21", "文件上传失败", "geoai.file.error.code.10321"),
    AUDIO_TRANSCODE_ERROR("22", "音频转码错误", "geoai.file.error.code.10322"),

    ;

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误码消息（默认）
     */
    private final String message;

    /**
     * 错误码国际化消息key
     */
    private final String messageKey;

    UosServiceErrorCode(String code, String message, String messageKey) {
        this.code = code;
        this.message = message;
        this.messageKey = messageKey;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getServiceModule() {
        return "03";
    }

}
