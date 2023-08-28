package com.imapcloud.sdk.pojo.entity;

public class FlightMessage {
    private String flightTips = "";
    private Integer state = -1;
    private Boolean readyToGo = false;

    public String getFlightTips() {
        return flightTips;
    }

    public void setFlightTips(String flightTips) {
        this.flightTips = flightTips;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Boolean getReadyToGo() {
        return readyToGo;
    }

    public void setReadyToGo(Boolean readyToGo) {
        this.readyToGo = readyToGo;
    }
}
