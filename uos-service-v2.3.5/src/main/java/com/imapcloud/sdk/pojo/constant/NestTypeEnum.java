package com.imapcloud.sdk.pojo.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wmin
 */
@Getter
@AllArgsConstructor
public enum NestTypeEnum {
    /**
     * P4R固定基站
     */
    G600(0, "G600基站", "geoai_uos_NestTypeEnum_01"),
    /**
     * mini基站第一代
     */
    S100_V1(1, "S100基站", "geoai_uos_NestTypeEnum_02"),
    /**
     * M300基站
     */
    G900(2, "G900基站", "geoai_uos_NestTypeEnum_03"),
    /**
     * 简易基站
     */
    T50(3, "T50基站", "geoai_uos_NestTypeEnum_04"),
    /**
     * 车载基站
     */
    CAR(4, "车载", "geoai_uos_NestTypeEnum_05"),
    /**
     * mini基站第二代
     */
    S100_V2(5, "S100_V2基站", "geoai_uos_NestTypeEnum_06"),

    /**
     * PM330基站
     */
    G503(6, "G503基站", "geoai_uos_NestTypeEnum_07"),


    S110_AUTEL(7, "S110通道基站", "geoai_uos_NestTypeEnum_08"),

    S110_MAVIC3(8, "S110御3基站", "geoai_uos_NestTypeEnum_11"),
    /**
     *
     */
    I_CREST2(100, "iCrest2基站", "geoai_uos_NestTypeEnum_09"),

    /**
     *大疆机场
     */
    DJI_DOCK(200, "大疆机场", "geoai_uos_NestTypeEnum_10"),

    /**
     * 大疆pilot
     */
    DJI_PILOT(201,"大疆pilot", "geoai_uos_NestTypeEnum_13"),

    G900_CHARGE(12,"G900充电式","geoai_uos_NestTypeEnum_12"),

    UNKNOWN(-1, "", "");
    private int value;

    private String message;

    private String key;

    public static NestTypeEnum getInstance(Integer value) {
        if (value == null) {
            return UNKNOWN;
        }
        for (NestTypeEnum e : NestTypeEnum.values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
