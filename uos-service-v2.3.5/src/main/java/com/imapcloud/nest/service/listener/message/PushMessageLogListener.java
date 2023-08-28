package com.imapcloud.nest.service.listener.message;

import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.service.event.message.PushMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PushMessagedLogListener.java
 * @Description PushMessagedLogListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class PushMessageLogListener extends AbstractEventListener<PushMessageEvent> {
    /**
     * 消息监听-日志处理
     *
     * @param pushMessageEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(PushMessageEvent pushMessageEvent) {
        log.info("【PushMessageLogListener】:{}",pushMessageEvent.toString());
        //doSomething
    }


}
