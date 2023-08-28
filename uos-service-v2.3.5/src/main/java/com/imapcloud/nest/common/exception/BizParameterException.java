package com.imapcloud.nest.common.exception;

/**
 * 业务参数异常
 * @author Vastfy
 * @date 2022/04/21 17:41
 * @since 1.8.9
 */
public class BizParameterException extends NestException {

    public BizParameterException(String message) {
        super(message);
    }

    public BizParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
