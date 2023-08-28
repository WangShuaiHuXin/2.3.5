package com.imapcloud.nest.v2.manager.ai;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 电力表计数据推送事件
 * @author Vastfy
 * @date 2022/12/5 13:33
 * @since 2.1.5
 */
public class PowerMeterDataPushedEvent extends ApplicationEvent {

    @Getter
    private final String orgCode;

    @Getter
    private final List<String> meterDetailIds;

    /**
     * Create a new {@code ApplicationEvent}.
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     * @param orgCode   单位编码
     * @param meterDetailIds 表计详情ID列表
     */
    public PowerMeterDataPushedEvent(Object source, String orgCode, List<String> meterDetailIds) {
        super(source);
        this.orgCode = orgCode;
        this.meterDetailIds = meterDetailIds;
    }
}
