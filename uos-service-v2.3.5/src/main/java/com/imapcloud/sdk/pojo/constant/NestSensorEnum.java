package com.imapcloud.sdk.pojo.constant;

import com.geoai.common.core.enums.IEnum;
import lombok.AllArgsConstructor;

/**
 * 传感器
 *
 * @author boluo
 * @date 2022-08-26
 */
@AllArgsConstructor
public enum NestSensorEnum implements IEnum<Integer> {
    /**
     * 传感器
     */
    HAN_HUA_QI(1, "喊话器", "geoai_uos_NestSensorEnum_01"),
    YE_HANG_DENG(2, "夜航灯", "geoai_uos_NestSensorEnum_02"),
    TAN_ZHAO_DENG(3, "探照灯", "geoai_uos_NestSensorEnum_03"),
    QI_TI_JIAN_CE(4, "气体检测", "geoai_uos_NestSensorEnum_04"),
    DEFAULT(-1, "", ""),
    ;

    public static NestSensorEnum getNestSensorEnumByCode(int code) {
        for (NestSensorEnum value : NestSensorEnum.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return DEFAULT;
    }

    private Integer code;

    private String message;

    private String key;
    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getKey() {return key;}
}
