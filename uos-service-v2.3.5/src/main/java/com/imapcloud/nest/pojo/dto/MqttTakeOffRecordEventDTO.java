package com.imapcloud.nest.pojo.dto;

public class MqttTakeOffRecordEventDTO {
    private String nestUuid;
    private Integer missionId;


    public String getNestUuid() {
        return nestUuid;
    }

    public void setNestUuid(String nestUuid) {
        this.nestUuid = nestUuid;
    }

    public Integer getMissionId() {
        return missionId;
    }

    public void setMissionId(Integer missionId) {
        this.missionId = missionId;
    }
}
