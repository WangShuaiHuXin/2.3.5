package com.imapcloud.nest.v2.manager.cps;

/**
 * motor指令
 *
 * @author boluo
 * @date 2023-03-27
 */
public interface MotorManager {

    /**
     * 降落引导
     *
     * @param nestId 巢id
     * @param which which
     */
    void landingGuidanceDown(String nestId, Integer which);

    /**
     * 无人机开机
     *
     * @param nestId 巢id
     */
    void aircraftOn(String nestId);

    /**
     * 无人机关机
     *
     * @param nestId 巢id
     */
    void aircraftOff(String nestId);
}
