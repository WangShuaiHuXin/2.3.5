package com.imapcloud.nest.service.listener.message;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.service.PubMessageService;
import com.imapcloud.nest.service.event.message.PushMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PushMessagedListener.java
 * @Description PushMessagedListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class PushMessageListener extends AbstractEventListener<PushMessageEvent> {

    @Resource
    private PubMessageService pubMessageService;
    /**
     * 消息监听-处理
     *
     * @param pushMessageEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(PushMessageEvent pushMessageEvent) {
        log.info("【PushMessageBeforeListener】:{}",pushMessageEvent.toString());
        List<PubMessageEntity> pubMessageEntityList = pushMessageEvent.getSource();
        if(CollectionUtil.isNotEmpty(pubMessageEntityList)){
            pubMessageEntityList.stream().forEach(pubMessage->{
                log.info("PushMessagedListener推送监听发起:{}",pubMessage.toString());
                this.pubMessageService.pushMain(pubMessage);
                log.info("PushMessagedListener推送监听结束");
            });
        }
    }

}
