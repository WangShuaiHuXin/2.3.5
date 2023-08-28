package com.imapcloud.nest.v2.common.enums;

import org.apache.xpath.operations.Bool;

import java.util.Arrays;
import java.util.List;

/**
 * GEOAI_DIAL_DEVICE_STATE
 */
public enum PowerDeviceStateEnum {

    /**
     * 未知
     */
    UKNOW(0, "未知"),
    /**
     * 正常
     */
    NORMAL(1, "正常"),
    /**
     * 一般缺陷
     */
    GNERMAL_DEFECTS(2, "一般缺陷"),
    /**
     * 严重缺陷
     */
    SERUIYS_DEFECTS(3, "严重缺陷"),
    /**
     * 危急缺陷
     */
    CRITICAL_DEFECTS(4, "危急缺陷"),
    ;
    private Integer code;
    private String value;

    PowerDeviceStateEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static String getValueByCode(Integer code) {
        PowerDeviceStateEnum[] typesEnums = values();
        for (PowerDeviceStateEnum typeenum : typesEnums) {
            if (typeenum.getCode().equals(code)) {
                return typeenum.getValue();
            }
        }
        return null;
    }

    public static Boolean isDefect(Integer code) {
        //正常数据
        Integer uknow = UKNOW.getCode();
        Integer normal = NORMAL.getCode();
        List<Integer> integerList = Arrays.asList(uknow, normal);
        if (integerList.contains(code)) {
            return false;
        }
        return true;
    }
}
