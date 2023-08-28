package com.imapcloud.nest.v2.common.enums;

/**
 * 红外测温，目前先不兼容单点标注的
 */
public enum PowerMeterInfraredDescEnum {
    MAX("MAX"),
    MIN("MIN"),
    AVG("AVG");
    private String desc;

    PowerMeterInfraredDescEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
