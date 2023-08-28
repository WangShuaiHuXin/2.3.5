package com.imapcloud.nest.v2.manager.event;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkDrawInDTO;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName ConfirmPicEvent.java
 * @Description 核实图片
 * @createTime 2022年07月15日 17:55:00
 */
public class ConfirmPicToResultEvent extends AbstractEvent<List<DataAnalysisMarkDrawInDTO>> {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public ConfirmPicToResultEvent(List<DataAnalysisMarkDrawInDTO> source) {
        super(source);
    }
}
