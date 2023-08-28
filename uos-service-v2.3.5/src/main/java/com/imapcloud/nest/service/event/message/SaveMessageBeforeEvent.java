package com.imapcloud.nest.service.event.message;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.pojo.dto.message.PubMessageSaveDTO;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName SaveMessageBeforeEvent.java
 * @Description 消息-保存前事件-校验判断、安全检查等
 * @createTime 2022年04月14日 17:55:00
 */
public class SaveMessageBeforeEvent<V> extends AbstractEvent<PubMessageSaveDTO> {

    /**
     * 用于存储需要赋值的对象
     */
    V v;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SaveMessageBeforeEvent(PubMessageSaveDTO source) {
        super(source);
    }

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SaveMessageBeforeEvent(PubMessageSaveDTO source , V v) {
        super(source);
        this.v =v;
    }

    public V getV(){
        return this.v;
    }
}
