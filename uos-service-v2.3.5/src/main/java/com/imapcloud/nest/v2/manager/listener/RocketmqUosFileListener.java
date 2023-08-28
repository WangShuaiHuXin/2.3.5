package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.json.JSONUtil;
import com.geoai.rocketmq.spring.annotation.RocketMQMessageListener;
import com.geoai.rocketmq.spring.core.MessageMetadata;
import com.geoai.rocketmq.spring.core.RocketMQListener;
import com.imapcloud.nest.v2.manager.dataobj.in.RocketmqInDO;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * rocketmq uos文件侦听器
 * 监听uos服务器文件处理消息，现阶段只有删除消息
 *
 * @author boluo
 * @date 2023-01-31
 * @deprecated 2.2.3，兼容CPS旧版切片，将在2023-06-30后续版本删除
 */
@Deprecated
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "geoai-uos-service", topic = "${geoai.uos.rocketmq.file-topic}")
public class RocketmqUosFileListener implements RocketMQListener<RocketmqInDO.MqInfo> {

    @Resource
    private FileManager fileManager;

    @Override
    public void onMessage(RocketmqInDO.MqInfo mqInfo, MessageMetadata messageMetadata) {

        log.info("#RocketmqUosFileListener.onMessage# messageMetadata={}", JSONUtil.toJsonStr(messageMetadata));
        if (mqInfo == null) {
            return;
        }
        try {
            log.info("#RocketmqUosFileListener.onMessage# start mqInfo={}", JSONUtil.toJsonStr(mqInfo));
            if (mqInfo.getType() == null) {
                return;
            }
            if (mqInfo.getType() == RocketmqInDO.MqTypeEnum.FILE_DELETE) {
                // 文件删除
                Object data = mqInfo.getData();
                if (data == null) {
                    return;
                }
                List<String> objectList = JSONUtil.toList(JSONUtil.toJsonStr(data), String.class);
                int i = 0;
                int batchSize = 100;
                List<String> batch = new ArrayList<>(batchSize);
                for (String object : objectList) {
                    i ++;
                    batch.add(object);
                    if(i >= batchSize){
                        fileManager.deleteFiles(batch);
                        batch.clear();
                    }
                }
                if(!CollectionUtils.isEmpty(batch)){
                    fileManager.deleteFiles(batch);
                }
            }
            log.info("#RocketmqUosFileListener.onMessage# end mqInfo={}", JSONUtil.toJsonStr(mqInfo));
        } catch (Exception e) {
            log.error("#RocketmqUosFileListener.onMessage# mqInfo={}, messageMetadata={}"
                    , JSONUtil.toJsonStr(mqInfo), JSONUtil.toJsonStr(messageMetadata), e);
            throw new RuntimeException(e);
        }
    }
}
