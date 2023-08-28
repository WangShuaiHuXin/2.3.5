package com.imapcloud.sdk.pojo.entity;

/**
 * @author wmin
 */
public class AirConditionState {

    private Integer mode;

    private Integer operate;

    private Integer introspect;

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getOperate() {
        return operate;
    }

    public void setOperate(Integer operate) {
        this.operate = operate;
    }

    public Integer getIntrospect() {
        return introspect;
    }

    public void setIntrospect(Integer introspect) {
        this.introspect = introspect;
    }

    enum Mode {
        MODE0(0, "unknown mode"),
        MODE1(1, "cryogen mode"),
        MODE2(2, "heat mode");
        private int num;
        private String represent;

        Mode(int num, String represent) {
            this.num = num;
            this.represent = represent;
        }

        public int getNum() {
            return num;
        }

        public String getRepresent() {
            return represent;
        }
    }

}
