package com.imapcloud.sdk.manager.camera.enums;

import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 禅思H20系列相机镜头模式
 */
public enum H20SeriesLensModeEnum {
    DEFAULT(0, "默认模式"),
    WIDE(1, "广角"),
    ZOOM(2, "变焦"),
    THRM(3, "热红外"),
    CURRENT(4, "当前图传画面"),
    ;
    private int value;
    private String express;

    H20SeriesLensModeEnum(int value, String express) {
        this.value = value;
        this.express = express;
    }

    public int getValue() {
        return value;
    }

    public String getExpress() {
        return express;
    }

    public static List<H20SeriesLensModeEnum> listLensModeEnums(List<Integer> valueList) {
        if (CollectionUtil.isNotEmpty(valueList)) {
            List<H20SeriesLensModeEnum> eList = new ArrayList<>(valueList.size());
            for (H20SeriesLensModeEnum e : H20SeriesLensModeEnum.values()) {
                if (valueList.contains(e.getValue())) {
                    eList.add(e);
                }
            }
            return eList;
        }
        return Collections.emptyList();
    }

    public static List<Integer> listValues(List<H20SeriesLensModeEnum> eList) {
        if (CollectionUtil.isNotEmpty(eList)) {
            List<Integer> valueList = eList.stream().map(H20SeriesLensModeEnum::getValue).collect(Collectors.toList());
            return valueList;
        }
        return Collections.emptyList();
    }
}
