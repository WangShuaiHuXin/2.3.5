package com.imapcloud.nest.v2.common.exception;

import com.geoai.common.core.exception.BizException;

/**
 * AI识别功能未授权<br/>
 * 详细定义见：{@link UosServiceErrorCode#AI_REC_FUNCTION_UNAUTHORIZED}
 * @author Vastfy
 * @date 2023/1/4 10:50
 * @since 2.1.7
 */
public class AIRecFunctionUnauthorizedException extends BizException {

    @Override
    public String getId() {
        // CODE: 10301
        return UosServiceErrorCode.AI_REC_FUNCTION_UNAUTHORIZED.toBizErrorCode();
    }

    public AIRecFunctionUnauthorizedException() {
    }

    public AIRecFunctionUnauthorizedException(String message) {
        super(message);
    }

    public AIRecFunctionUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

}
