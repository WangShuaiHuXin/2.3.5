package com.imapcloud.sdk.pojo.constant.dji;

public enum RemoteDebugMethodEnum {
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
    CHARGE_CLOSE("charge_close", "关闭充电");

    private String code;
    private String express;

    RemoteDebugMethodEnum(String code, String express) {
        this.code = code;
        this.express = express;
    }

}
