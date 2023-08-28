package com.imapcloud.nest.service.listener.message;

import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.service.event.message.DeleteMessageBeforeEvent;
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
public class DeleteMessageBeforeListener extends AbstractEventListener<DeleteMessageBeforeEvent> {

    /**
     * 消息删除前监听-判断是否存在内容
     *
     * @param deleteMessageBeforeEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(DeleteMessageBeforeEvent deleteMessageBeforeEvent) {
        log.info("【DeleteMessageBeforeListener】:{}",deleteMessageBeforeEvent.toString());
    }


}
