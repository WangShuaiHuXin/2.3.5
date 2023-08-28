package com.imapcloud.sdk.pojo.entity;

import java.util.Objects;

/**
 * @author wmin
 * 新的电机状态，目前只有m300是这样的，后面可能都会更新成这样
 */
public class MotorBaseState {
    /**
     * EDC(Energy dispatch center)状态
     */
    private State EDCState = new State();
    /**
     * 机械抓状态
     */
    private State gripperState = new State();
    /**
     * 机械臂X状态
     */
    private State armXState = new State();
    /**
     * 机械臂Y状态
     */
    private State armYState = new State();
    /**
     * 机械臂Z状态
     */
    private State armZState = new State();
    /**
     * 舱门状态
     */
    private State cabinState = new State();

    /**
     * 舱门A状态
     */
    private State cabinAState = new State();

    /**
     * 舱门B状态
     */
    private State cabinBState = new State();

    /**
     * 升降平台状态
     */
    private State liftState = new State();
    /**
     * 一体化归中状态
     */
    private State gatherState = new State();
    /**
     * X归中状态
     */
    private State gatherXState = new State();
    /**
     * Y归中状态
     */
    private State gatherYState = new State();

    private State squareXState = new State();

    private State squareYState = new State();

    private State rotatePlatformState = new State();

    /**
     * 固定装置A
     */
    private State droneFixAState = new State();

    /**
     * 固定装置B
     */
    private State droneFixBState = new State();

    /**
     * 固定装置C
     */
    private State droneFixCState = new State();


    public State getEDCState() {
        return EDCState;
    }

    public void setEDCState(State EDCState) {
        if (EDCState != null) {
            this.EDCState = EDCState;
        }
    }

    public State getGripperState() {
        return gripperState;
    }

    public void setGripperState(State gripperState) {
        if (gripperState != null) {
            this.gripperState = gripperState;
        }
    }

    public State getArmXState() {
        return armXState;
    }

    public void setArmXState(State armXState) {
        if (armXState != null) {
            this.armXState = armXState;
        }
    }

    public State getArmYState() {
        return armYState;
    }

    public void setArmYState(State armYState) {
        if (armYState != null) {
            this.armYState = armYState;
        }
    }

    public State getArmZState() {
        return armZState;
    }

    public void setArmZState(State armZState) {
        if (armZState != null) {
            this.armZState = armZState;
        }
    }

    public State getCabinState() {
        return cabinState;
    }

    public void setCabinState(State cabinState) {
        if (cabinState != null) {
            this.cabinState = cabinState;
        }
    }

    public State getLiftState() {
        return liftState;
    }

    public void setLiftState(State liftState) {
        if (liftState != null) {
            this.liftState = liftState;
        }
    }

    public State getGatherState() {
        return gatherState;
    }

    public void setGatherState(State gatherState) {
        if (gatherState != null) {
            this.gatherState = gatherState;
        }
    }

    public State getGatherXState() {
        return gatherXState;
    }

    public void setGatherXState(State gatherXState) {
        if (gatherXState != null) {
            this.gatherXState = gatherXState;
        }
    }

    public State getGatherYState() {
        return gatherYState;
    }

    public void setGatherYState(State gatherYState) {
        if (gatherYState != null) {
            this.gatherYState = gatherYState;
        }
    }

    public State getCabinAState() {
        return cabinAState;
    }

    public void setCabinAState(State cabinAState) {
        if (Objects.nonNull(cabinAState)) {
            this.cabinAState = cabinAState;
        }
    }

    public State getCabinBState() {
        return cabinBState;
    }

    public void setCabinBState(State cabinBState) {
        if (Objects.nonNull(cabinBState)) {
            this.cabinBState = cabinBState;
        }
    }

    public State getRotatePlatformState() {
        return rotatePlatformState;
    }

    public void setRotatePlatformState(State rotatePlatformState) {
        if (Objects.nonNull(rotatePlatformState)) {
            this.rotatePlatformState = rotatePlatformState;
        }
    }

    public State getDroneFixAState() {
        return droneFixAState;
    }

    public void setDroneFixAState(State droneFixAState) {
        if(Objects.nonNull(droneFixAState)) {
            this.droneFixAState = droneFixAState;
        }
    }

    public State getDroneFixBState() {
        return droneFixBState;
    }

    public void setDroneFixBState(State droneFixBState) {
        if(Objects.nonNull(droneFixBState)) {
            this.droneFixBState = droneFixBState;
        }
    }

    public State getDroneFixCState() {
        return droneFixCState;
    }

    public void setDroneFixCState(State droneFixCState) {
        if(Objects.nonNull(droneFixCState)) {
            this.droneFixCState = droneFixCState;
        }
    }

    public State getSquareXState() {
        return squareXState;
    }

    public void setSquareXState(State squareXState) {
        if(Objects.nonNull(squareXState)) {
            this.squareXState = squareXState;
        }
    }

    public State getSquareYState() {
        return squareYState;
    }

    public void setSquareYState(State squareYState) {
        if(Objects.nonNull(squareYState)) {
            this.squareYState = squareYState;
        }
    }

    public static class State {
        /**
         * 电机设备状态
         */
        private Integer deviceState = -1;
        /**
         * 电机错误码
         */
        private Integer errorCode = -1;

        /**
         * 电机运行状态
         */
        private Integer runningState = -1;
        /**
         * 电机传感器状态
         */
        private Integer sensorState = -1;

        public Integer getDeviceState() {
            return deviceState;
        }

        public void setDeviceState(Integer deviceState) {
            if (deviceState != null) {
                this.deviceState = deviceState;
            }
        }

        public Integer getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(Integer errorCode) {
            if (errorCode != null) {
                this.errorCode = errorCode;
            }
        }

        public Integer getRunningState() {
            return runningState;
        }

        public void setRunningState(Integer runningState) {
            if (runningState != null) {
                this.runningState = runningState;
            }
        }

        public Integer getSensorState() {
            return sensorState;
        }

        public void setSensorState(Integer sensorState) {
            if (sensorState != null) {
                this.sensorState = sensorState;
            }
        }
    }
}
