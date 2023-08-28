package com.imapcloud.sdk.manager;

import com.imapcloud.sdk.mqttclient.DjiClient;
import com.imapcloud.sdk.pojo.constant.dji.TslTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DjiMqttResParam<T> {
    /**
     * 客户端
     */
    private DjiClient client;

    /**
     * 传输的主题
     */
    private String topic;
    /**
     * 传输指令
     */
    private String method;
    /**
     * 传输参数
     */
    private Object param;

    /**
     * Java对象类型
     */
    private Class clazz;
    /**
     * 最大等待时间
     */
    private Integer maxWaitTime;

    private TslTypeEnum tslTypeEnum;

    /**
     * 业务id
     */
    private String bid;

    /**
     * 事物id
     */
    private String tid;

    /**
     * 网格设备
     */
    private String gateway;



    public DjiMqttResParam(DjiClient client, String topic) {
        this.client = client;
        this.topic = topic;
    }

}
