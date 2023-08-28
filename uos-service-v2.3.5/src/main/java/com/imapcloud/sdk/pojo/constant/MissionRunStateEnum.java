package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 */

public enum MissionRunStateEnum {
    NOT_DEFINED("00000"),
    MISSION_UPLOAD("00001"),
    MISSION_START("00002"),
    OBSTACLE_MISSION_START("00003"),
    SET_HOME_LOCATION("00004"),
    MISSION_FINISH("00005"),
    DOWNLOAD_MISSION_PHOTO("00006"),
    NEST_RECYCLE_FINISH("00007");

    private String value;

    MissionRunStateEnum(String value) {
        this.value = value;
    }

    public static MissionRunStateEnum getInstance(String value) {
        switch (value) {
            case "00000":
                return NOT_DEFINED;
            case "00001":
                return MISSION_UPLOAD;
            case "00002":
                return MISSION_START;
            case "00003":
                return OBSTACLE_MISSION_START;
            case "00004":
                return SET_HOME_LOCATION;
            case "00005":
                return MISSION_FINISH;
            case "00006":
                return DOWNLOAD_MISSION_PHOTO;
            case "00007":
                return NEST_RECYCLE_FINISH;
            default:
                return null;
        }
    }

}
