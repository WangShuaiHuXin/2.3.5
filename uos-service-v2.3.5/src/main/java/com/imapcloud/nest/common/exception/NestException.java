package com.imapcloud.nest.common.exception;

/**
 * @author wmin
 * 全局异常处理类
 */
public class NestException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String msg;
    private int code = 500;

    public NestException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public NestException(String msg, Throwable e) {
        super(msg, e);
        this.setMsg(msg);
    }

    public NestException(String msg, int code) {
        super(msg);
        this.setMsg(msg);
        this.setCode(code);
    }

    public NestException(String msg, int code, Throwable e) {
        super(msg, e);
        this.setMsg(msg);
        this.setCode(code);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
