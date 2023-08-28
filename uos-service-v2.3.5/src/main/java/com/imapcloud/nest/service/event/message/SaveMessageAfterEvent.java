package com.imapcloud.nest.service.event.message;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.model.PubMessageEntity;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName SaveMessageAfterEvent.java
 * @Description 消息-保存后事件-主要用于处理回写、回滚
 * @createTime 2022年04月14日 17:55:00
 */
public class SaveMessageAfterEvent<V> extends AbstractEvent<PubMessageEntity> {

    V v;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SaveMessageAfterEvent(PubMessageEntity source) {
        super(source);
    }

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SaveMessageAfterEvent(PubMessageEntity source , V v) {
        super(source);
        this.v = v;
    }

    public V getV(){
        return v;
    }


}
