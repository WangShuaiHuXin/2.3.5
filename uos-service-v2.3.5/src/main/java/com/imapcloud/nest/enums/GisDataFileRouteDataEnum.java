package com.imapcloud.nest.enums;

/**
 * 电机状态枚举
 * @author daolin
 */
public enum GisDataFileRouteDataEnum {
    //以这里的为准，暂时没用字典
    /*0:影像,1:地形,2:模型,3航线，4矢量，5其他*/
    IMAGE(0, "影像"),
    TERAIN(1, "地形"),
    MODEL(2, "模型"),
    AILLINE(3, "航线"),
    VECTOR(4, "矢量"),
    OTHER(5, "其他");
    private Integer code;
    private String state;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    GisDataFileRouteDataEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }
}
