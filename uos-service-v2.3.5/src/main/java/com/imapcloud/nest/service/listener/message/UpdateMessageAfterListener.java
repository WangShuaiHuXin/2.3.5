package com.imapcloud.nest.service.listener.message;

import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.service.event.message.UpdateMessageAfterEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UpdateMessageAfterListener.java
 * @Description UpdateMessageAfterListener
 * @createTime 2022年04月14日 15:12:00
 */
@Slf4j
@Service
public class UpdateMessageAfterListener extends AbstractEventListener<UpdateMessageAfterEvent> {
    /**
     * 消息更新后监听-日志处理
     *
     * @param updateMessageAfterEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(UpdateMessageAfterEvent updateMessageAfterEvent) {
        log.info("【UpdateMessageAfterListener】:{}",updateMessageAfterEvent.toString());
    }


}
