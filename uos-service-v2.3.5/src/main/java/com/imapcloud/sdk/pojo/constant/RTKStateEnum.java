package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 * 只支持固定机巢
 */
public enum RTKStateEnum {
    NONE("NONE", "无","geoai_uos_rtkstate_enum_none"),
    SINGLE_POINT("SINGLE_POINT", "单点解","geoai_uos_rtkstate_enum_single_point"),
    FLOAT("FLOAT", "浮点解","geoai_uos_rtkstate_enum_float"),
    FIXED_POINT("FIXED_POINT", "固定解","geoai_uos_rtkstate_enum_fixed_point"),
    UNKNOWN("UNKNOWN", "未知","geoai_uos_rtkstate_enum_unknown");
    private String value;
    private String chinese;
    private String key;



    RTKStateEnum(String value, String chinese) {
        this.value = value;
        this.chinese = chinese;
    }

    RTKStateEnum(String value, String chinese, String key) {
        this.value = value;
        this.chinese = chinese;
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public String getChinese() {
        return chinese;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static RTKStateEnum getInstance(String value) {
        return valueOf(value);
    }
}
