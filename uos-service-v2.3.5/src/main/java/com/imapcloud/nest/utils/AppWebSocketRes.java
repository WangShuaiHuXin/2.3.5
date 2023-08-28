package com.imapcloud.nest.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by wmin on 2020/12/1 17:43
 *
 * @author wmin
 */
@Data
public class AppWebSocketRes implements Serializable {
    private static final long serialVersionUID = 1L;

    private String token;
    private Integer messageType;
    private Map<String, Object> messageBody;

    public static AppWebSocketRes instance() {
        return new AppWebSocketRes();
    }

    public AppWebSocketRes token(String token) {
        this.setToken(token);
        return this;
    }

    public AppWebSocketRes messageType(Integer messageType) {
        this.setMessageType(messageType);
        return this;
    }

    public AppWebSocketRes messageBody(Map<String, Object> messageBody) {
        this.setMessageBody(messageBody);
        return this;
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }
}
