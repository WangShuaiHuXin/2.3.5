package com.imapcloud.nest.common.listener;

import org.springframework.context.event.EventListener;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName AbstractEventListener.java
 * @Description 监听者
 * @createTime 2022年03月17日 17:53:00
 */
public abstract class AbstractEventListener<E> {

    /**
     * 消息监听-处理
     * @param e 消息事件
     */
    @EventListener
    public abstract void eventListener(E e);

}
