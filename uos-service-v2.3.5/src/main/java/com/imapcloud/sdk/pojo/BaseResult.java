package com.imapcloud.sdk.pojo;


public class BaseResult implements IBaseResult{
    private String code;
    private String pCode;
    private String msg;
    private String date;
    private Integer which = 0;

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

    public Integer getWhich() {
        return which;
    }

    public void setWhich(Integer which) {
        this.which = which;
    }
}
