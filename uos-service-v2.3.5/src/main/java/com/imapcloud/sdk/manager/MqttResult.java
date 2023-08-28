package com.imapcloud.sdk.manager;

import java.util.List;

public class MqttResult<T> {
    private T res;
    private List<T> resList;
    private String msg;
    private boolean success;
    private boolean timeout = false;

    public T    getRes() {
        return res;
    }

    public void setRes(T res) {
        this.res = res;
    }

    public List<T> getResList() {
        return resList;
    }

    public void setResList(List<T> resList) {
        this.resList = resList;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public MqttResult<T> msg(String msg) {
        this.setMsg(msg);
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public MqttResult<T> success(boolean success) {
        this.setSuccess(success);
        return this;
    }

    public static void copyProperties(MqttResult s, MqttResult t) {
        t.setRes(s.getRes());
        t.setSuccess(s.isSuccess());
        t.setResList(s.getResList());
        t.setMsg(s.getMsg());

    }

    public static <T> MqttResult<T> err(String msg) {
        MqttResult<T> mr = new MqttResult<>();
        mr.setMsg(msg);
        mr.setSuccess(false);
        return mr;
    }


}
