package com.imapcloud.nest.v2.common.web.rest;

import com.geoai.common.web.rest.ErrorLevel;

public enum PilotCommonErrorCode {
    OK(0, "请求成功", "geoai_common_success_request"),
    BUSINESS_HANDLE_FAILED(500, "", "geoai_common_business_processing_failure"),
;

    private final Integer code;
    private final String message;
    private final String messageKey;

    private PilotCommonErrorCode(Integer code, String message, String messageKey) {
        this.code = code;
        this.message = message;
        this.messageKey = messageKey;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

    public String getMessage() {
        return this.message;
    }

    public ErrorLevel getErrorLevel() {
        return ErrorLevel.SYSTEM;
    }

    public String getServiceModule() {
        return "0";
    }
}
