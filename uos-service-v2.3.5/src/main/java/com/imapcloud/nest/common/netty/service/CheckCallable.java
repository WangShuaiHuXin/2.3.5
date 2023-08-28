package com.imapcloud.nest.common.netty.service;

import com.imapcloud.nest.common.netty.service.CheckTool;
import com.imapcloud.nest.pojo.dto.CheckDto;

import java.util.concurrent.Callable;

/**
 * 检查任务类
 *
 * @author daolin
 */
public class CheckCallable implements Callable {
    private String uuid;
    private String topic;
    private Integer time;

    public CheckCallable(String uuid, String topic, Integer time) {
        this.uuid = uuid;
        this.topic = topic;
        this.time = time;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public CheckDto call() throws Exception {
        CheckTool checkTool = new CheckTool();
        for (int i = 0; i < time * 2; i++) {
            CheckDto result = checkTool.getStateByTopic(topic, uuid);
            if (result.getPass() == 1) {
                return result;
            } else if (result.getPass() == 2) {
                Thread.sleep(500);
            } else if (result.getPass() == 0) {
                return result;
            }
        }
        //超时返回
        CheckDto checkTimeout = checkTool.getStateByTopic(topic, uuid);
        checkTimeout.setPass(3);
        return checkTimeout;
    }
}
