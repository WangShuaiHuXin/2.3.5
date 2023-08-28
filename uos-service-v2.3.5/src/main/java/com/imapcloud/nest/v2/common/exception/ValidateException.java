package com.imapcloud.nest.v2.common.exception;

import com.geoai.common.core.exception.BizException;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName ValidateException.java
 * @Description ValidateException
 * @createTime 2022年07月19日 14:30:00
 */
public class ValidateException extends BizException {
    private static final long serialVersionUID = 1L;
    private String msg;

    public ValidateException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public ValidateException(String msg, Throwable e) {
        super(msg, e);
        this.setMsg(msg);
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
