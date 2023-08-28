package com.imapcloud.nest.utils;

import com.alibaba.fastjson.JSON;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回给前端的websocket包装类
 *
 * @author wmin
 */
@Data
public class WebSocketRes implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 20000 - success
     * 50000 - error
     */
    private int code;

    private String msg;
    /**
     * 机巢uuid
     */
    private String uuid;
    /**
     * 发送过去代表的主题
     */
    private WebSocketTopicEnum topic;
    /**
     * 相关的Dto
     * nestDto,
     * AerographyDto
     * AircraftDto
     */
    private Object param;

    public static WebSocketRes instance() {
        return new WebSocketRes();
    }

    public static WebSocketRes ok() {
        WebSocketRes wr = new WebSocketRes();
        wr.setCode(20000);
        wr.setMsg("success");
        return wr;
    }

    public static WebSocketRes err() {
        WebSocketRes wr = new WebSocketRes();
        wr.setCode(50000);
        wr.setMsg("error");
        return wr;
    }

    public WebSocketRes code(int code) {
        setCode(code);
        return this;
    }

    public WebSocketRes msg(String msg) {
        setMsg(msg);
        return this;
    }

    public WebSocketRes uuid(String uuid) {
        setUuid(uuid);
        return this;
    }

    public WebSocketRes topic(WebSocketTopicEnum topic) {
        setTopic(topic);
        return this;
    }

    public WebSocketRes data(Map<String, Object> param) {
        setParam(param);
        return this;
    }

    public WebSocketRes data(Object param) {
        setParam(param);
        return this;
    }

    public WebSocketRes data(String key, Object value) {
        Map<String, Object> data = new HashMap<>(2);
        data.put(key, value);
        setParam(data);
        return this;
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }


}
