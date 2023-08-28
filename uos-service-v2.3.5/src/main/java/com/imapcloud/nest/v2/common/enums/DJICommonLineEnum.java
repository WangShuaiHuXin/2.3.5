package com.imapcloud.nest.v2.common.enums;

import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJICommonLineEnum.java
 * @Description DJICommonLineEnum
 * @createTime 2022年07月08日 15:41:00
 */
public enum DJICommonLineEnum {

    //Common
    DEBUG_MODE_OPEN("debug_mode_open", "调试模式开启"),
    DEBUG_MODE_CLOSE("debug_mode_close", "调试模式关闭"),
    SUPPLEMENT_LIGHT_OPEN("supplement_light_open", "打开补光灯"),
    SUPPLEMENT_LIGHT_CLOSE("supplement_light_close", "关闭补光灯"),
    RETURN_HOME("return_home", "一键返航"),
    DEVICE_REBOOT("device_reboot", "机场重启"),
    DRONE_OPEN("drone_open", "飞行器开机"),
    DRONE_CLOSE("drone_close", "飞行器关机"),
    DEVICE_FORMAT("device_format", "机场数据格式化"),
    COVER_OPEN("cover_open", "打开舱盖"),
    COVER_CLOSE("cover_close", "关闭舱盖"),
    PUTTER_OPEN("putter_open", "推杆展开"),
    PUTTER_CLOSE("putter_close", "推杆闭合"),
    CHARGE_OPEN("charge_open", "打开充电"),
    CHARGE_CLOSE("charge_close", "关闭充电"),

    //Live
    LIVE_START_PUSH("live_start_push", "推流开启"),
    LIVE_CLOSE_PUSH("live_close_push","推流关闭")

    ;

    private String code;
    private String str;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    DJICommonLineEnum(String code, String str) {
        this.code = code;
        this.str = str;
    }

    public static List toList() {
        //Lists.newArrayList()其实和new ArrayList()几乎一模
        //  一样, 唯一它帮你做的(其实是javac帮你做的), 就是自动推导(不是"倒")尖括号里的数据类型.
        List list = Lists.newArrayList();

        for (DJICommonLineEnum dataAnalysisPicTypeEnum : DJICommonLineEnum.values()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", dataAnalysisPicTypeEnum.getCode());
            map.put("name", dataAnalysisPicTypeEnum.getStr());
            list.add(map);
        }
        return list;
    }

}
