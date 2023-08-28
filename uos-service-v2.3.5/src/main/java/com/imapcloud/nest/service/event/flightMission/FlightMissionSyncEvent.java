package com.imapcloud.nest.service.event.flightMission;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.pojo.vo.FlightMissionSyncAggVO;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName FlightMissionSyncEvent.java
 * @Description FlightMissionSyncEvent
 * @createTime 2022年03月24日 15:44:00
 */
public class FlightMissionSyncEvent extends AbstractEvent<FlightMissionSyncAggVO> {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public FlightMissionSyncEvent(FlightMissionSyncAggVO source) {
        super(source);
    }
}