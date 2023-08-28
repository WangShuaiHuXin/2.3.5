package com.imapcloud.nest.enums;


/**
 * 任务识别类型
 *
 * @author kings
 */
public enum TaskIdentificationTypeEnum {
    DEFECT_IDENTIFICATION(0, "外观检测"),
    METER_READING(1, "表计读数"),
    INFRARED_THERMOMETRY(2, "红外测温");

    private Integer value;
    private String desc;

    TaskIdentificationTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
