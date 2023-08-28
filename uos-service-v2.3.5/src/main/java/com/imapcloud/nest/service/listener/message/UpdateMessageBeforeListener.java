package com.imapcloud.nest.service.listener.message;

import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.service.event.message.UpdateMessageBeforeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UpdateMessagedBeforeListener.java
 * @Description UpdateMessagedBeforeListener
 * @createTime 2022年04月14日 15:12:00
 */
@Slf4j
@Service
public class UpdateMessageBeforeListener extends AbstractEventListener<UpdateMessageBeforeEvent> {

    /**
     * 消息更新前监听-日志处理
     *
     * @param updateMessageBeforeEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(UpdateMessageBeforeEvent updateMessageBeforeEvent) {
        log.info("【UpdateMessageBeforeListener】:{}",updateMessageBeforeEvent.toString());
    }


}
