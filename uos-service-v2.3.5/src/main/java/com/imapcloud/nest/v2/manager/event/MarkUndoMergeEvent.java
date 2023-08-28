package com.imapcloud.nest.v2.manager.event;

import com.imapcloud.nest.common.event.AbstractEvent;

import java.util.List;

/**
 * 撤回核实，问题是否属于分组处理
 */
public class MarkUndoMergeEvent extends AbstractEvent<List<Long>> {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public MarkUndoMergeEvent(List<Long> source) {
        super(source);
    }
}
