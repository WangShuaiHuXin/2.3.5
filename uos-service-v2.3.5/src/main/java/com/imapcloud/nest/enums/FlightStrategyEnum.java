package com.imapcloud.nest.enums;

/**
 * 飞行策略
 *
 * @author wmin
 */
public enum FlightStrategyEnum {
    /**
     * 安全优先策略，先飞到航点，再执行转换机头的动作,使用AUTO的航向模式
     */
    SAFETY_FIRST(0),
    /**
     * 效率有限策略，无人机边飞边转，使用USING_WAYPOINT_HEADING的航向模式
     */
    EFFICIENCY_FIRST(1);

    private int value;

    FlightStrategyEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
