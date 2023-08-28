package com.imapcloud.nest.service.listener.message;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.service.event.message.CancelDelayPushEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName CancelDelayListener.java
 * @Description CancelDelayListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class CancelDelayPushListener extends AbstractEventListener<CancelDelayPushEvent> {
    /**
     * 取消延迟推送监听-处理
     *
     * @param cancelDelayPushEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(CancelDelayPushEvent cancelDelayPushEvent) {
        //TODO 取消延迟任务
        log.info("【CancelDelayPushEvent】取消延迟任务监听:{}","start");
        List<PubMessageEntity> pubMessageEntityList = cancelDelayPushEvent.getSource();
        if(CollectionUtil.isNotEmpty(pubMessageEntityList)){

            pubMessageEntityList.stream().forEach(pubMessage->{

            });
        }
    }

}
