package com.imapcloud.sdk.pojo.entity;

/**
 * @author wmin
 * 迷你机巢电机状态
 */
public class MiniMotorState {
    private Integer cabinState;
    private Integer chargeState;
    private Integer syncState;
    private Integer liftState;

    public MiniMotorState() {
        this.cabinState = 0;
        this.chargeState = 0;
        this.syncState = 0;
        this.liftState = 0;
    }

    public Integer getCabinState() {
        return cabinState;
    }

    public void setCabinState(Integer cabinState) {
        if (cabinState != null) {
            this.cabinState = cabinState;
        }
    }

    public Integer getChargeState() {
        return chargeState;
    }

    public void setChargeState(Integer chargeState) {
        if (chargeState != null) {
            this.chargeState = chargeState;
        }
    }

    public Integer getSyncState() {
        return syncState;
    }

    public void setSyncState(Integer syncState) {
        if (syncState != null) {
            this.syncState = syncState;
        }
    }

    public Integer getLiftState() {
        return liftState;
    }

    public void setLiftState(Integer liftState) {
        if (liftState != null) {
            this.liftState = liftState;
        }
    }
}
