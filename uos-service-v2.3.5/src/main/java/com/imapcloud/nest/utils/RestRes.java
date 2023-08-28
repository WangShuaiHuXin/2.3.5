package com.imapcloud.nest.utils;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wmin
 * 项目返回类
 * @deprecated at 2022/07/13，使用{@link com.geoai.common.web.rest.Result}代替
 */
@Deprecated
public class RestRes implements Serializable {
    private static final long serialVersionUID = 1L;
    private int code;
    private String msg;
    private Map<String, Object> param;

    public RestRes() {
        this.code = 20000;
        this.msg = "success";
        this.param = null;
    }

    public static RestRes ok() {
        return new RestRes();
    }

    public static RestRes ok(String msg) {
        RestRes nestRes = new RestRes();
        nestRes.setMsg(msg);
        return nestRes;
    }

    public static RestRes ok(Map<String, Object> param) {
        RestRes nestRes = new RestRes();
        nestRes.setParam(param);
        return nestRes;
    }

    public static RestRes ok(Map<String, Object> param,String msg) {
        RestRes nestRes = new RestRes();
        nestRes.msg(msg);
        nestRes.setParam(param);
        return nestRes;
    }

    public RestRes code(Integer code) {
        this.code = code;
        return this;
    }

    public RestRes msg(String msg) {
        this.msg = msg;
        return this;
    }

    public static RestRes err() {
        RestRes nestRes = new RestRes();
        nestRes.setCode(50000);
        nestRes.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_AUTH_UNKNOWN_ERROR_PLEASE_CONTACT_THE_ADMINISTRATOR.getContent()));
        return nestRes;
    }

    public static RestRes errorParam() {
        return errorParam(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    public static RestRes errorParam(String msg) {
        RestRes nestRes = new RestRes();
        nestRes.setCode(40000);
        nestRes.setMsg(msg);
        return nestRes;
    }

    public static RestRes err(String msg) {
        RestRes nestRes = new RestRes();
        nestRes.setMsg(msg);
        nestRes.setCode(50000);
        return nestRes;
    }

    public static RestRes err(Map<String, Object> param) {
        RestRes nestRes = new RestRes();
        nestRes.setParam(param);
        nestRes.setMsg("error");
        nestRes.setCode(50000);
        return nestRes;
    }

    public static RestRes err(String key, Object value) {
        RestRes nestRes = new RestRes();
        Map<String, Object> param = new HashMap<>(2);
        param.put(key, value);
        nestRes.setParam(param);
        nestRes.setMsg("error");
        nestRes.setCode(50000);
        return nestRes;
    }


    public static RestRes err(int code, String msg) {
        RestRes nestRes = new RestRes();
        nestRes.setCode(code);
        nestRes.setMsg(msg);
        return nestRes;
    }

    public static RestRes noDataWarn() {
        RestRes restRes = new RestRes();
        restRes.setCode(40004);
        restRes.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_AUTH_THE_RELATED_DATA_CANNOT_BE_QUERIED.getContent()));
        return restRes;
    }

    public static RestRes warn(String msg) {
        RestRes restRes = new RestRes();
        restRes.setCode(40004);
        restRes.setMsg(msg);
        return restRes;
    }


    public static RestRes ok(String key, Object value) {
        RestRes nestRes = new RestRes();
        Map<String, Object> param = new HashMap<>(2);
        param.put(key, value);
        nestRes.setParam(param);
        return nestRes;
    }

    public static RestRes ok(String key, Object value, String msg) {
        RestRes nestRes = new RestRes();
        Map<String, Object> param = new HashMap<>(2);
        param.put(key, value);
        nestRes.setParam(param);
        nestRes.setMsg(msg);
        return nestRes;
    }

    public boolean isOk() {
        return this.getCode() == 20000;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}
