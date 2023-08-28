package com.imapcloud.nest.v2.common.enums;

import org.apache.commons.compress.utils.Lists;

import java.util.*;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJITypeEnum.java
 * @Description DJITypeEnum
 * @createTime 2022年07月08日 15:41:00
 */
public enum DJILivePayloadEnum {

    AIR_CRAFT_SELF(0, "无人机机身摄像头"),
    AIR_CRAFT_LIVE(1, "无人机相机摄像头"),
    DJI_DOCK_LIVE(2, "基站监控")
    ;

    private Integer code;
    private String str;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getState() {
        return str;
    }

    public void setState(String str) {
        this.str = str;
    }

    DJILivePayloadEnum(Integer code, String str) {
        this.code = code;
        this.str = str;
    }

    public static List toList() {
        List list = Lists.newArrayList();

        for (DJILivePayloadEnum dataAnalysisPicTypeEnum : DJILivePayloadEnum.values()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", dataAnalysisPicTypeEnum.getCode());
            map.put("name", dataAnalysisPicTypeEnum.getState());
            list.add(map);
        }
        return list;
    }

}
