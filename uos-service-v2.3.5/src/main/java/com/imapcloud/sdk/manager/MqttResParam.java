package com.imapcloud.sdk.manager;

import cn.hutool.core.bean.BeanUtil;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.mqttclient.DjiClient;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MqttResParam<T> {
    /**
     * 客户端
     */
    private Client client;

    /**
     * 传输的主题
     */
    private String topic;
    /**
     * 传输指令
     */
    private String code;
    /**
     * 传输参数
     */
    private Map<String, Object> param;
    /**
     * 返回的参数形式
     */
    private Type type;
    /**
     * Java对象类型
     */
    private Class<T> clazz;
    /**
     * 单个参数的key
     */
    private String key;
    /**
     * 最大等待时间
     */
    private Integer maxWaitTime;

    /**
     * 设备编号
     */
    private AirIndexEnum[] which;

    public MqttResParam() {

    }

    public MqttResParam(Client client, String topic) {
        this.client = client;
        this.topic = topic;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public MqttResParam<T> client(Client client) {
        this.client = client;
        return this;
    }


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MqttResParam<T> topic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MqttResParam<T> code(String code) {
        this.code = code;
        return this;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }


    public MqttResParam<T> param(Map<String, Object> param) {
        this.param = param;
        return this;
    }

    public MqttResParam<T> param(String key, Object val) {
        this.param = new HashMap<>(2);
        this.param.put(key, val);
        return this;
    }

    public MqttResParam<T> param(Object param) {
        this.param = BeanUtil.beanToMap(param);
        return this;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public MqttResParam<T> type(Type type) {
        this.type = type;
        return this;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    public MqttResParam<T> clazz(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MqttResParam<T> key(String key) {
        this.key = key;
        return this;
    }

    public MqttResParam<T> key(String key, Class<T> clazz) {
        this.key = key;
        this.clazz = clazz;
        return this;
    }

    public Integer getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(Integer maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public MqttResParam<T> maxWaitTime(Integer maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
        return this;
    }

    public AirIndexEnum[] getWhich() {
        return which;
    }

    public void setWhich(AirIndexEnum[] which) {
        this.which = which;
    }

    public MqttResParam<T> which(AirIndexEnum... which) {
        if (which != null && which.length > 0) {
            this.which = which;
        } else {
            this.which = new AirIndexEnum[]{AirIndexEnum.ONE};
        }
        return this;
    }

    public static <T> MqttResParam<T> getInstance(Client client, String topic) {
        return new MqttResParam<>(client, topic);
    }

    public enum Type {
        /**
         * 无返回值
         */
        NUL,
        /**
         * 单个属性
         */
        ATT,
        /**
         * 对象
         */
        OBJ,
        /**
         * 列表
         */
        LIS,
        /**
         * 原数据
         */
        ORI
    }
}
