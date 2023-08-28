package com.imapcloud.nest.v2.common.web.rest;

import com.geoai.common.core.util.DateUtils;
import com.geoai.common.core.util.I18nMessageUtils;
import com.geoai.common.web.rest.CommonErrorCode;
import com.geoai.common.web.rest.IResult;
import com.geoai.common.web.util.MessageUtils;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

public class PilotResult<T> implements IResult<T> {
    private Integer code;
    private String message;
    private T data;
    private final String traceId = MDC.get("TRACE_ID");
    private final long timestamp = DateUtils.toTimestamp(LocalDateTime.now());

    public PilotResult() {
    }

    public static <T> PilotResult<T> ok() {
        return (PilotResult<T>) ok((Object)null);
    }

    public static <T> PilotResult<T> ok(T data) {
        PilotResult<T> result = new PilotResult();
        result.code = PilotCommonErrorCode.OK.getCode();
        result.message = I18nMessageUtils.getMessage(CommonErrorCode.OK.getMessageKey());
        result.data = data;
        return result;
    }

    public static <T> PilotResult<T> error(String msg) {
        return error(PilotCommonErrorCode.BUSINESS_HANDLE_FAILED, msg);
    }

    public static <T> PilotResult<T> error(PilotCommonErrorCode pilotCommonErrorCode) {
        return error(pilotCommonErrorCode, (String)null);
    }

    public static <T> PilotResult<T> error(PilotCommonErrorCode pilotCommonErrorCode, String msg) {
        PilotResult<T> result = new PilotResult();
        result.code = pilotCommonErrorCode.getCode();
        String messageKey = pilotCommonErrorCode.getMessageKey();
        if (!StringUtils.hasText(msg)) {
            msg = MessageUtils.getMessage(messageKey);
        }

        if (!StringUtils.hasText(msg)) {
            msg = pilotCommonErrorCode.getMessage();
        }
        result.message = msg;
        return result;
    }

    @Override
    public boolean isOk(){
        return Objects.equals(this.getCode(), "0");
    }

    @Override
    public String getCode() {
        return String.valueOf(this.code);
    }

    @Override
    public String getMsg() {
        return this.message;
    }

    @Override
    public T getData() {
        return this.data;
    }

    @Override
    public String toString() {
        return "Result{code='" + this.code + '\'' + ", msg='" + this.message + '\'' + ", data=" + this.data + ", traceId='" + this.traceId + '\'' + ", timestamp=" + this.timestamp + '}';
    }

    public String getTraceId() {
        return this.traceId;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}

