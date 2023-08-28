package com.imapcloud.sdk.manager.media.enums;

public enum MediaCurrentStateEnum {
    UNKNOWN("UNKNOWN", "未知"),
    IDLE("IDLE", "空闲"),
    STARTING_UP("STARTING_UP", "无人机启动中"),
    DOWNLOADING("DOWNLOADING", "下载中"),
    UPLOADING("UPLOADING", "上传中"),
    DOWNLOAD_UPLOAD_MEANWHILE("DOWNLOAD_UPLOAD_MEANWHILE", "边下载边上传"),
    FORMATTING("FORMATTING", "格式化中"),
    DELETING("DELETING", "删除中");
    private String value;
    private String express;

    public String getValue() {
        return value;
    }


    public String getExpress() {
        return express;
    }


    MediaCurrentStateEnum(String value, String express) {
        this.value = value;
        this.express = express;
    }

    public static MediaCurrentStateEnum getInstance(String value) {
        if (value != null) {
            for (MediaCurrentStateEnum e : MediaCurrentStateEnum.values()) {
                if (e.getValue().equals(value)) {
                    return e;
                }
            }
        }
        return UNKNOWN;
    }
}
