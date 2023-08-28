package com.imapcloud.sdk.pojo.constant;

public enum MediaStateEnum {
    UNKNOWN("UNKNOWN", "未知"),
    DOWNLOADING("DOWNLOADING", "下载中"),
    DELETING("DELETING", "删除中"),
    FORMATTING("FORMATTING", "格式化中");
    private String value;
    private String chinese;

    MediaStateEnum(String value, String chinese) {
        this.value = value;
        this.chinese = chinese;
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

    public static MediaStateEnum getInstance(String value) {
        return valueOf(value);
    }
}
