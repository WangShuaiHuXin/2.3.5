package com.imapcloud.sdk.pojo.entity;

import com.imapcloud.sdk.pojo.constant.MissionStateEnum;

/**
 * @author wmin
 * 航点状态
 */
public class WaypointStateCPSV1 {
    private String execMissionID;
    private Integer missionReachIndex;
    private MissionStateEnum missionState;
    private Integer missionUploadedIndex;

    public String getExecMissionID() {
        return execMissionID;
    }

    public void setExecMissionID(String execMissionID) {
        this.execMissionID = execMissionID;
    }

    public Integer getMissionReachIndex() {
        return missionReachIndex;
    }

    public void setMissionReachIndex(Integer missionReachIndex) {
        this.missionReachIndex = missionReachIndex;
    }

    public MissionStateEnum getMissionState() {
        return missionState;
    }

    public void setMissionState(MissionStateEnum missionState) {
        this.missionState = missionState;
    }

    public Integer getMissionUploadedIndex() {
        return missionUploadedIndex;
    }

    public void setMissionUploadedIndex(Integer missionUploadedIndex) {
        this.missionUploadedIndex = missionUploadedIndex;
    }
}
