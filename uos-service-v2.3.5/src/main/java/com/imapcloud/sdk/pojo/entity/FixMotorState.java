package com.imapcloud.sdk.pojo.entity;

/**
 * @author wmin
 * 电机状态
 */
public class FixMotorState {
    //TODO:等待文档更新注释
    private Integer armStepState;
    private Integer armXState;
    private Integer armYState;
    private Integer armZState;
    private Integer cabin1State;
    private Integer cabin2State;
    private Integer liftState;
    private Integer signStepAngle;
    private Integer signStepState;
    private Integer squareXState;
    private Integer squareY1State;
    private Integer squareY2State;
    private Integer turnState;

    public FixMotorState() {

        this.armStepState = 0;
        this.armXState = 0;
        this.armYState = 0;
        this.armZState = 0;
        this.cabin1State = 0;
        this.cabin2State = 0;
        this.liftState = 0;
        this.signStepAngle = 0;
        this.signStepState = 0;
        this.squareXState = 0;
        this.squareY1State = 0;
        this.squareY2State = 0;
        this.turnState = 0;
    }

    public Integer getArmStepState() {
        return this.armStepState;
    }

    public void setArmStepState(Integer armStepState) {
        if (armStepState != null) {
            this.armStepState = armStepState;
        }

    }

    public Integer getArmXState() {
        return this.armXState;
    }

    public void setArmXState(Integer armXState) {
        if (armXState != null) {
            this.armXState = armXState;
        }

    }

    public Integer getArmYState() {
        return this.armYState;
    }

    public void setArmYState(Integer armYState) {
        if (armYState != null) {
            this.armYState = armYState;
        }

    }

    public Integer getArmZState() {
        return this.armZState;
    }

    public void setArmZState(Integer armZState) {
        if (armZState != null) {
            this.armZState = armZState;
        }

    }

    public Integer getCabin1State() {
        return this.cabin1State;
    }

    public void setCabin1State(Integer cabin1State) {
        if (cabin1State != null) {
            this.cabin1State = cabin1State;
        }

    }

    public Integer getCabin2State() {
        return this.cabin2State;
    }

    public void setCabin2State(Integer cabin2State) {
        if (cabin2State != null) {
            this.cabin2State = cabin2State;
        }

    }

    public Integer getLiftState() {
        return this.liftState;
    }

    public void setLiftState(Integer liftState) {
        if (liftState != null) {
            this.liftState = liftState;
        }

    }

    public Integer getSignStepAngle() {
        return this.signStepAngle;
    }

    public void setSignStepAngle(Integer signStepAngle) {
        if (signStepAngle != null) {
            this.signStepAngle = signStepAngle;
        }

    }

    public Integer getSignStepState() {
        return this.signStepState;
    }

    public void setSignStepState(Integer signStepState) {
        if (signStepState != null) {
            this.signStepState = signStepState;
        }

    }

    public Integer getSquareXState() {
        return this.squareXState;
    }

    public void setSquareXState(Integer squareXState) {
        if (squareXState != null) {
            this.squareXState = squareXState;
        }

    }

    public Integer getSquareY1State() {
        return this.squareY1State;
    }

    public void setSquareY1State(Integer squareY1State) {
        if (squareY1State != null) {
            this.squareY1State = squareY1State;
        }

    }

    public Integer getSquareY2State() {
        return this.squareY2State;
    }

    public void setSquareY2State(Integer squareY2State) {
        if (squareY2State != null) {
            this.squareY2State = squareY2State;
        }

    }

    public Integer getTurnState() {
        return this.turnState;
    }

    public void setTurnState(Integer turnState) {
        if (turnState != null) {
            this.turnState = turnState;
        }
    }
}
