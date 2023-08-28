package com.imapcloud.nest.v2.common.exception;

import com.geoai.common.core.exception.BizException;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName BusinessException.java
 * @Description BusinessException
 * @createTime 2022年07月19日 14:30:00
 */
public class BusinessException extends BizException {
    private static final long serialVersionUID = 1L;
    private String msg;

    public BusinessException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BusinessException(String msg, Throwable e) {
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
