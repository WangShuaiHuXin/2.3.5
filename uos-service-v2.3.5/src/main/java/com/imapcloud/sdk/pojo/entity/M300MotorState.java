package com.imapcloud.sdk.pojo.entity;

/**
 * @author wmin
 */
public class M300MotorState {
    private Integer EDC;
    private Integer armStepState;
    private Integer armXState;
    private Integer armYState;
    private Integer armZState;
    private Integer liftState;
    private Integer signStepAngle;
    private Integer signStepState;
    private Integer telescopic;
    private Integer turnState;
    private Integer cabinState;

    public Integer getEDC() {
        return EDC;
    }

    public void setEDC(Integer EDC) {
        if (EDC != null) {
            this.EDC = EDC;
        }
    }

    public Integer getArmStepState() {
        return armStepState;
    }

    public void setArmStepState(Integer armStepState) {
        if (armStepState != null) {
            this.armStepState = armStepState;
        }
    }

    public Integer getArmXState() {
        return armXState;
    }

    public void setArmXState(Integer armXState) {
        if (armXState != null) {
            this.armXState = armXState;
        }
    }

    public Integer getArmYState() {
        return armYState;
    }

    public void setArmYState(Integer armYState) {
        if (armYState != null) {
            this.armYState = armYState;
        }
    }

    public Integer getArmZState() {
        return armZState;
    }

    public void setArmZState(Integer armZState) {
        if (armZState != null) {
            this.armZState = armZState;
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

    public Integer getSignStepAngle() {
        return signStepAngle;
    }

    public void setSignStepAngle(Integer signStepAngle) {
        if (signStepAngle != null) {
            this.signStepAngle = signStepAngle;
        }
    }

    public Integer getSignStepState() {
        return signStepState;
    }

    public void setSignStepState(Integer signStepState) {
        if (signStepState != null) {
            this.signStepState = signStepState;
        }
    }

    public Integer getTelescopic() {
        return telescopic;
    }

    public void setTelescopic(Integer telescopic) {
        if (telescopic != null) {
            this.telescopic = telescopic;
        }
    }

    public Integer getTurnState() {
        return turnState;
    }

    public void setTurnState(Integer turnState) {
        if (turnState != null) {
            this.turnState = turnState;
        }
    }

    public Integer getCabinState() {
        return cabinState;
    }

    public void setCabinState(Integer cabinState) {
        if(cabinState != null) {
            this.cabinState = cabinState;
        }
    }
}
