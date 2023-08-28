package com.imapcloud.sdk.pojo.entity;

import com.imapcloud.sdk.pojo.constant.MissionExecStateEnum;
import com.imapcloud.sdk.pojo.constant.MissionStateEnum;

/**
 * @author wmin
 * 二代机巢航点状态
 */
public class WaypointState {
    /**
     * 执行的任务Id
     */
    private String execMissionID;
    /**
     * 任务当前到达的点
     */
    private Integer missionReachIndex;
    /**
     * 任务状态
     */
    private MissionStateEnum missionState;
    /**
     * 任务上传
     */
    private Integer missionUploadedIndex;
    /**
     * 任务执行状态（单个航点的状态）
     */
    private MissionExecStateEnum missionExecState;

    private Integer missionTotalIndex;

    private String missionID;

    private Boolean isWaypointReached;


    public WaypointState() {
        this.execMissionID = "";
        this.missionReachIndex = 0;
        this.missionState = MissionStateEnum.UNKNOWN;
        this.missionUploadedIndex = 0;
        this.missionExecState = MissionExecStateEnum.UNKNOWN;
        this.missionTotalIndex = -1;
        this.missionID = "";
        this.isWaypointReached = false;
    }

    public String getExecMissionID() {
        return this.execMissionID;
    }

    public void setExecMissionID(String execMissionID) {
        if (execMissionID != null) {
            this.execMissionID = execMissionID;
        }

    }

    public Integer getMissionReachIndex() {
        return this.missionReachIndex;
    }

    public void setMissionReachIndex(Integer missionReachIndex) {
        if (missionReachIndex != null) {
            this.missionReachIndex = missionReachIndex;
        }

    }

    public MissionStateEnum getMissionState() {
        return this.missionState;
    }

    public void setMissionState(MissionStateEnum missionState) {
        if (missionState != null) {
            this.missionState = missionState;
        }

    }

    public Integer getMissionUploadedIndex() {
        return this.missionUploadedIndex;
    }

    public void setMissionUploadedIndex(Integer missionUploadedIndex) {
        if (missionUploadedIndex != null) {
            this.missionUploadedIndex = missionUploadedIndex;
        }

    }

    public MissionExecStateEnum getMissionExecState() {
        return this.missionExecState;
    }

    public void setMissionExecState(MissionExecStateEnum missionExecState) {
        if (missionExecState != null) {
            this.missionExecState = missionExecState;
        }

    }

    public Integer getMissionTotalIndex() {
        return missionTotalIndex;
    }

    public void setMissionTotalIndex(Integer missionTotalIndex) {
        if (missionTotalIndex != null) {
            this.missionTotalIndex = missionTotalIndex;
        }
    }

    public String getMissionID() {
        return missionID;
    }

    public void setMissionID(String missionID) {
        if (missionID != null) {
            this.missionID = missionID;
        }
    }

    public Boolean getIsWaypointReached() {
        return isWaypointReached;
    }

    public void setIsWaypointReached(Boolean isWaypointReached) {
        this.isWaypointReached = isWaypointReached;
    }
}
