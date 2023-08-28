package com.imapcloud.sdk.pojo.constant;

public enum MediaStatusV2Enum {
    UNKNOWN("UNKNOWN", "未知"),
    IDLE("IDLE", "空闲"),
    DOWNLOADING("DOWNLOADING", "下载中"),
    UPLOADING("UPLOADING", "上传中"),
    DOWNLOAD_UPLOAD_MEANWHILE("DOWNLOAD_UPLOAD_MEANWHILE", "边下载边上传");

    private String value;
    private String chinese;

    MediaStatusV2Enum(String value, String chinese) {
        this.value = value;
        this.chinese = chinese;
    }

    public static MediaStatusV2Enum getInstance(String value) {
        return valueOf(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }
}
