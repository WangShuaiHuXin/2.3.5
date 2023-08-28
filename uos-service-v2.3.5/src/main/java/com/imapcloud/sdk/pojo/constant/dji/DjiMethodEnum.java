package com.imapcloud.sdk.pojo.constant.dji;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wmin
 */
public enum DjiMethodEnum {
    /**
     * en 必须与大疆保持一致
     */
    UNKNOWN("unknown", "未知"),
    DEBUG_MODE_OPEN("debug_mode_open", "调试模式开"),
    DEBUG_MODE_CLOSE("debug_mode_close", "调试模式关"),
    SUPPLEMENT_LIGHT_CLOSE("supplement_light_close", "关闭补光灯"),
    SUPPLEMENT_LIGHT_OPEN("supplement_light_open", "打开补光灯"),
    RETURN_HOME("return_home", "一键返航"),
    BATTERY_MAINTENANCE_SWITCH("battery_maintenance_switch", "电池保养状态切换"),
    ALARM_STATE_SWITCH("alarm_state_switch", "声光报警开关"),
    BATTERY_STORE_MODE_SWITCH("battery_store_mode_switch", "电池保存模式切换"),
    DEVICE_REBOOT("device_reboot", "机场重启"),
    DRONE_OPEN("drone_open", "飞行器开机"),
    DRONE_CLOSE("drone_close", "飞行器关机"),
    DEVICE_FORMAT("device_format", "机场数据格式化"),
    DRONE_FORMAT("device_format", "飞机数据格式化"),
    COVER_OPEN("cover_open", "打开舱盖"),
    COVER_CLOSE("cover_close", "关闭舱盖"),
    PUTTER_OPEN("putter_open", "推杆展开"),
    PUTTER_CLOSE("putter_close", "推杆闭合"),
    CHARGE_OPEN("charge_open", "打开充电"),
    CHARGE_CLOSE("charge_close", "关闭充电"),
    SDR_WORKMODE_SWITCH("sdr_workmode_switch", "增强图传开关"),
    ;
    private String en;
    private String zh;

    DjiMethodEnum(String en, String zh) {
        this.en = en;
        this.zh = zh;
    }

    public static DjiMethodEnum getInstance(String en) {
        if (Objects.nonNull(en)) {
            Optional<DjiMethodEnum> first = Arrays.stream(DjiMethodEnum.values()).filter(e -> e.getEn().equals(en)).findFirst();
            return first.orElse(UNKNOWN);
        }
        return UNKNOWN;
    }

    public String getEn() {
        return en;
    }

    public String getZh() {
        return zh;
    }
}
