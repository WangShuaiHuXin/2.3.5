package com.imapcloud.nest.v2.service.impl;

import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.in.RocketmqInDO;
import com.imapcloud.nest.v2.manager.rest.RocketmqManager;
import com.imapcloud.nest.v2.service.RocketmqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * rocketmq服务
 *
 * @author boluo
 * @date 2023-01-31
 */
@Slf4j
@Service
public class RocketmqServiceImpl implements RocketmqService {

    @Resource
    private RocketmqManager rocketmqManager;

    @Value("${geoai.uos.rocketmq.file-topic}")
    private String fileDeleteTopic;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    private static final String DEFAULT_TAG = "tag1";

    @Override
    public boolean sendFileDelete(List<String> objectList) {
        RocketmqInDO.MqInfo mqInfo = new RocketmqInDO.MqInfo();
        mqInfo.setType(RocketmqInDO.MqTypeEnum.FILE_DELETE);
        mqInfo.setData(objectList);
        try {
            rocketmqManager.send(fileDeleteTopic, DEFAULT_TAG, mqInfo);
            return true;
        } catch (Exception e) {
            log.warn("#RocketmqServiceImpl.sendFileDelete# failed");
        }
        return false;
    }

    @Override
    public boolean sendTask(RocketmqInDO.TaskMqInfo taskMqInfo) {
        RocketmqInDO.MqInfo mqInfo = new RocketmqInDO.MqInfo();
        mqInfo.setType(RocketmqInDO.MqTypeEnum.POWER_TASK);
        mqInfo.setData(taskMqInfo);
        try {
            rocketmqManager.send(geoaiUosProperties.getRocketmq().getPowerTaskTopic(), DEFAULT_TAG, mqInfo);
            return true;
        } catch (Exception e) {
            log.warn("#RocketmqServiceImpl.sendTask# failed");
        }
        return false;
    }
}
