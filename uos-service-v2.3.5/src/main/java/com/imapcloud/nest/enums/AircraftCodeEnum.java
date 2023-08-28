package com.imapcloud.nest.enums;

import java.util.LinkedList;
import java.util.List;

/**
 * @author wmin
 */

public enum AircraftCodeEnum {
    /**
     * 无人机型号
     */
    PHANTOM3_PROFESSIONAL(7, "精灵 PHANTOM 3 专业版", 23, "geoai_uos_AircraftCodeEnum_01"),
    PHANTOM3_ADVANCED(8, "精灵 PHANTOM 3 advanced", 23, "geoai_uos_AircraftCodeEnum_02"),
    PHANTOM3_STANDARD(9, "精灵 PHANTOM 3 standard", 25, "geoai_uos_AircraftCodeEnum_03"),
    PHANTOM3_4k(10, "精灵 PHANTOM 3 4k", 25,"geoai_uos_AircraftCodeEnum_04"),

    PHANTOM4_RTK(1, "精灵 PHANTOM 4 RTK", 30, "geoai_uos_AircraftCodeEnum_05"),
    PHANTOM4_PRO(11, "精灵 PHANTOM 4 进阶版", 30, "geoai_uos_AircraftCodeEnum_06"),
    PHANTOM4_PRO_V2(12, "精灵 PHANTOM 4 进阶版V2", 30, "geoai_uos_AircraftCodeEnum_07"),
    PHANTOM_4_ADVANCED(13, "精灵 PHANTOM 4 advanced", 30, "geoai_uos_AircraftCodeEnum_08"),

    MAVIC2_ENTERPRISE(2, "御 MAVIC 2 行业版", 31, "geoai_uos_AircraftCodeEnum_09"),
    MAVIC2_ENTERPRISE_DUAL(3, "御 MAVIC 2 行业双光版", 31, "geoai_uos_AircraftCodeEnum_10"),
    MAVIC2_PRO(4, "御 MAVIC 2 行业进阶版", 31, "geoai_uos_AircraftCodeEnum_11"),
    //    MAVIC2_RTK(14, "御 MAVIC 2 RTK", 31),
    MAVIC2_ZOOM(15, "御 MAVIC 2 变焦版", 31, "geoai_uos_AircraftCodeEnum_12"),
    MAVIC_AIR(16, "御 MAVIC AIR", 21, "geoai_uos_AircraftCodeEnum_13"),
    MAVIC_PRO(17, "御 MAVIC 进阶版", 27, "geoai_uos_AircraftCodeEnum_14"),
    MAVIC2(31, "御 MAVIC", 31, "geoai_uos_AircraftCodeEnum_15"),

    MAVIC3(32,"御 MAVIC 3",46, "geoai_uos_AircraftCodeEnum_16"),

    MATRICE_300_RTK(5, "经纬 MATRICE 300 RTK", 55, "geoai_uos_AircraftCodeEnum_17"),
    MATRICE_200(18, "经纬 MATRICE 200", 27, "geoai_uos_AircraftCodeEnum_18"),
    MATRICE_210(19, "经纬 MATRICE 210", 27, "geoai_uos_AircraftCodeEnum_19"),
    MATRICE_210_RTK(20, "经纬 MATRICE 210 RTK", 27, "geoai_uos_AircraftCodeEnum_20"),
    MATRICE_200_V2(21, "经纬 MATRICE 210 V2", 24, "geoai_uos_AircraftCodeEnum_21"),
    MATRICE_210_RTK_V2(22, "经纬 MATRICE 210 RTK V2", 24, "geoai_uos_AircraftCodeEnum_22"),
    MATRICE_600(23, "经纬 MATRICE 600", 30, "geoai_uos_AircraftCodeEnum_23"),
    MATRICE_600_PRO(24, "经纬 MATRICE 600 PRO", 30, "geoai_uos_AircraftCodeEnum_24"),
    MATRICE_100(25, "经纬 MATRICE 100", 16, "geoai_uos_AircraftCodeEnum_25"),

    AUTEL_EVO2(6, "道通 EVO 2", 20, "geoai_uos_AircraftCodeEnum_26"),
    AUTEL_EVO2_4G(33, "道通 EVO 2 4G", 20, "geoai_uos_AircraftCodeEnum_27"),

    INSPIRE_2(26, "悟 INSPIRE 2", 23, "geoai_uos_AircraftCodeEnum_28"),
    INSPIRE_1_PRO(27, "悟 INSPIRE 进阶版", 15, "geoai_uos_AircraftCodeEnum_29"),
    INSPIRE_1(28, "悟 INSPIRE 进阶版", 18, "geoai_uos_AircraftCodeEnum_30"),

    A3(29, "A3", 0, "geoai_uos_AircraftCodeEnum_31"),
    N3(30, "N3", 0, "geoai_uos_AircraftCodeEnum_32"),

    /**
     * 大疆M30
     */
    M30(200, "经纬M30", 0, "geoai_uos_AircraftCodeEnum_33"),
    M30T(201, "经纬M30T", 0, "geoai_uos_AircraftCodeEnum_34"),

    UNKNOWN(-1, "未知", -1, "geoai_uos_AircraftCodeEnum_35");

    private int value;
    private String code;
    private int batteryLifeTime;

    private String key;


    AircraftCodeEnum(int value, String code, int batteryLifeTime, String key) {
        this.value = value;
        this.code = code;
        this.batteryLifeTime = batteryLifeTime;
        this.key = key;
    }

    public static AircraftCodeEnum getInstance(String value) {
        if (value != null) {
            AircraftCodeEnum[] values = AircraftCodeEnum.values();
            for (AircraftCodeEnum ace : values) {
                if (ace.getValue() == Integer.parseInt(value)) {
                    return ace;
                }
            }
        }
        return UNKNOWN;
    }

    private static List<Integer> ALL_VALUE;
    static {
        ALL_VALUE = new LinkedList<>();
        for (AircraftCodeEnum value : AircraftCodeEnum.values()) {
            ALL_VALUE.add(value.getValue());
        }
    }

    public static boolean checkCode(int value) {
        return ALL_VALUE.contains(value);
    }

    public int getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public int getBatteryLifeTime() {
        return batteryLifeTime;
    }

    public String getKey() {return key;}
}
