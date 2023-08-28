package com.imapcloud.nest.v2.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum PowerInspectionExcelItemEnum {

    ANA_SCREENSHOTS(0, "分析截图"),
    PATROL_PHOTOS(1, "巡检照片"),
    PART_NAME(2, "部件名称"),
    DEVICE_NAME(3, "设备名称"),
    ANA_TYPE(4, "分析类型"),
    ANA_RESULT(5, "分析结果"),
    ANA_CONCLUSION(6, "分析结论"),
    DEVICE_TYPE(7, "设备类型"),
    SPACING(8, "间隔单元"),
    VOLTAGE_LEVEL(9, "电压等级"),
    PHOTO_TIME(-1, "拍摄时间"),
    ;
    private Integer code;
    private String value;

    public static List<String> getValueByIds(List<String> ids) {
        List<String> names = new ArrayList<>();
        PowerInspectionExcelItemEnum[] values = values();
        for (PowerInspectionExcelItemEnum powerInspectionExcelItemEnum : values) {
            if (ids.contains(powerInspectionExcelItemEnum.getCode().toString())) {
                names.add(powerInspectionExcelItemEnum.value);
            }
        }
        return names;
    }

    public static String getValueById(String id) {
        List<String> names = new ArrayList<>();
        PowerInspectionExcelItemEnum[] values = values();
        for (PowerInspectionExcelItemEnum powerInspectionExcelItemEnum : values) {
            if (powerInspectionExcelItemEnum.getCode() == Integer.valueOf(id)) {
                return powerInspectionExcelItemEnum.value;
            }
        }
        return "";
    }
}
