package com.imapcloud.nest.enums;

/**
 * 航线类型枚举类
 *
 * @Author: wmin
 * @Date: 2021/4/13 10:25
 */
public enum AirLineTypeEnum {

    UNIFY_AIR_LINE(1, "易飞云平台统一航线格式"),
    POINT_CLOUD_AIR_LINE(2, "点云、易飞、航线格式"),
    NEST_AIR_LINE(3, "机巢航线格式");
    private int value;
    private String express;

    AirLineTypeEnum(int value, String express) {
        this.value = value;
        this.express = express;
    }

    public static AirLineTypeEnum getInstance(int value) {
        for (AirLineTypeEnum alt : AirLineTypeEnum.values()) {
            if (alt.getValue() == value) {
                return alt;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }
}
