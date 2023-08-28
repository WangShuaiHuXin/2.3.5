package com.imapcloud.sdk.pojo;

import lombok.ToString;

@ToString()
public class BaseResult3 {
    private String code;
    private String pCode;
    private String msg;
    private String date;
    private String param;
    private String traceId;
    private Integer which;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getpCode() {
        return pCode;
    }

    public void setpCode(String pCode) {
        this.pCode = pCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Integer getWhich() {
        return which;
    }

    public void setWhich(Integer which) {
        this.which = which;
    }
}
