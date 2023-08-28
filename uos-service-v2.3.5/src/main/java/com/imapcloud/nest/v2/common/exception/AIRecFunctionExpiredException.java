package com.imapcloud.nest.v2.common.exception;

import com.geoai.common.core.exception.BizException;

/**
 * AI识别功能已失效<br/>
 * 详细定义见：{@link UosServiceErrorCode#SOME_AI_REC_FUNCTION_EXPIRED}
 * @author Vastfy
 * @date 2022/10/26 10:50
 * @since 2.1.4
 */
public class AIRecFunctionExpiredException extends BizException {

    @Override
    public String getId() {
        // CODE: 10300
        return UosServiceErrorCode.SOME_AI_REC_FUNCTION_EXPIRED.toBizErrorCode();
    }

    public AIRecFunctionExpiredException() {
    }

    public AIRecFunctionExpiredException(String message) {
        super(message);
    }

    public AIRecFunctionExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

}
