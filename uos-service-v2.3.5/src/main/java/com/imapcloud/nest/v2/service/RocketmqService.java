package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.manager.dataobj.in.RocketmqInDO;

import java.util.List;

/**
 * rocketmq服务
 *
 * @author boluo
 * @date 2023-01-31
 */
public interface RocketmqService {

    /**
     * 发送文件删除消息
     *
     * @param objectList 对象
     * @return boolean
     */
    boolean sendFileDelete(List<String> objectList);

    /**
     * 发送任务
     *
     * @param taskMqInfo 任务mq信息
     */
    boolean sendTask(RocketmqInDO.TaskMqInfo taskMqInfo);
}
