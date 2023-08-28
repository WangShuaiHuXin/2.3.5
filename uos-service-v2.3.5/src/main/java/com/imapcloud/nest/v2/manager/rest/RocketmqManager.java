package com.imapcloud.nest.v2.manager.rest;

import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.dataobj.in.RocketmqInDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.java.message.MessageBuilderImpl;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * rocketmq生产者
 *
 * @author boluo
 * @date 2023-01-31
 */
@Slf4j
@Component
public class RocketmqManager {

    @Resource
    private Producer producer;

    public void send(String topic, String tag, RocketmqInDO.MqInfo mqInfo) {
        try {
            log.info("#RocketmqManager.send# topic={}, tag={}, mqInfo={}", topic, tag, mqInfo);

            Message message = new MessageBuilderImpl()
                    .setTopic(topic)
                    .setTag(tag)
                    .setBody(JSONUtil.toJsonStr(mqInfo).getBytes(StandardCharsets.UTF_8))
                    .build();
            SendReceipt send = producer.send(message);
            log.info("#RocketmqManager.send# topic={}, tag={}, mqInfo={}, send={}", topic, tag, mqInfo, send);
        } catch (Exception e) {
            log.error("#RocketmqManager.send# topic={}, tag={}, mqInfo={}", topic, tag, mqInfo, e);
            throw new BusinessException("send rocketMQ msg error.");
        }
    }
}
