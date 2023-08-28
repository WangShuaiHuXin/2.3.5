package com.imapcloud.nest.enums;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName NestExternalEquipEnum.java
 * @Description NestExternalEquipEnum
 * @createTime 2022年06月22日 09:39:00
 */
public enum NestExternalEquipEnum {

    EXTERNAL_EQUIP_0(0, "其他"),
    EXTERNAL_EQUIP_1(1, "气体"),
    EXTERNAL_EQUIP_2(2, "喊话器");

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

    NestExternalEquipEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }
}
