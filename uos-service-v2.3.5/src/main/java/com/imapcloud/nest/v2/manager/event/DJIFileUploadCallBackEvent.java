package com.imapcloud.nest.v2.manager.event;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.v2.manager.dataobj.in.DjiFileUploadCallBackDO;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIFileUploadCallBackEvent.java
 * @Description 大疆文件回调事件
 * @createTime 2022年07月15日 17:55:00
 */
public class DJIFileUploadCallBackEvent extends AbstractEvent<DjiFileUploadCallBackDO> {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DJIFileUploadCallBackEvent(DjiFileUploadCallBackDO source) {
        super(source);
    }
}
