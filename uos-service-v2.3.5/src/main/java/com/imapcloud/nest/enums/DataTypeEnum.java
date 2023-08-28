package com.imapcloud.nest.enums;

/**
 * 数据中心类型枚举值
 */
public enum DataTypeEnum {
    PHOTO(0,"图片"),
    VIDEO(1,"视频"),
    ORTHO(2,"正射"),
    POINTCLOUD(3,"点云"),
    TILT(4,"倾斜"),
    VECTOR(5,"矢量"),
    PANORAMA(6,"全景"),
    AIR(7,"气体"),
    POLLUTION_GRID(8, "污染网格"),
    MULTISPECTRAL(9,"多光谱");

    private int value;
    private String express;
    DataTypeEnum(int value, String express) {
        this.value = value;
        this.express = express;
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
