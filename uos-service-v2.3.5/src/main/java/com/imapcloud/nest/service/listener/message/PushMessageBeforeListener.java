package com.imapcloud.nest.service.listener.message;

import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.service.event.message.PushMessageBeforeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PushMessageBeforeListener.java
 * @Description PushMessageBeforeListener
 * @createTime 2022年04月14日 15:12:00
 */
@Slf4j
@Service
public class PushMessageBeforeListener extends AbstractEventListener<PushMessageBeforeEvent> {
    /**
     * 消息推送前监听-逻辑校验 -可拆分，可合并为一个校验
     *
     * @param pushMessageBeforeEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(PushMessageBeforeEvent pushMessageBeforeEvent) {
        log.info("【PushMessageBeforeListener】:{}",pushMessageBeforeEvent.toString());
    }


}
