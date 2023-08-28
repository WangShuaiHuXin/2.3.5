package com.imapcloud.sdk.pojo.constant;

import com.geoai.common.web.util.MessageUtils;

public enum NestStateEnum {
    INIT("INIT", "初始化", "init","geoai_uos_neststateenum_init"),
    SELF_TEST("SELF_TEST", "自检中","self test","geoai_uos_neststateenum_self_test"),
    WAIT_RESET("WAIT_RESET", "等待重置", "wait reset","geoai_uos_neststateenum_wait_reset"),
    RESETTING("RESETTING", "重置中", "resetting", "geoai_uos_neststateenum_resetting"),
    STANDBY("STANDBY", "待机", "standby","geoai_uos_neststateenum_standby"),
    PREPARING("PREPARING", "启动中", "preparing", "geoai_uos_neststateenum_preparing"),
    OPEN_WITHOUT_BATTERY("OPEN_WITHOUT_BATTERY", "未装载开启模式", "open without battery","geoai_uos_neststateenum_open_without_battery"),
    READY_TO_GO("READY_TO_GO", "准备起飞", "ready to go","geoai_uos_neststateenum_ready_to_go"),
    EXECUTING("EXECUTING", "执行任务中", "executing","geoai_uos_neststateenum_executing"),
    GOING_HOME("GOING_HOME", "返航", "going home", "geoai_uos_neststateenum_going_home"),
    LANDING("LANDING", "着陆中", "landing","geoai_uos_neststateenum_landing"),
    RECOVERING("RECOVERING", "回收中", "recovering","geoai_uos_neststateenum_recovering"),
    BATTERY_LOADING("BATTERY_LOADING", "电池装载中", "battery loading","geoai_uos_neststateenum_battery_loading"),
    BATTERY_UNLOADING("BATTERY_UNLOADING", "电池卸载中", "battery unloading","geoai_uos_neststateenum_battery_unloading"),
    BATTERY_LOADED("BATTERY_LOADED", "电池已装载", "battery loaded","geoai_uos_neststateenum_battery_loaded"),
    ERROR("ERROR", "状态错误", "error","geoai_uos_neststateenum_error"),
    TAKE_OFF("TAKE_OFF", "起飞中", "take off","geoai_uos_neststateenum_take_off"),
    OPERATING("OPERATING", "操作中", "operating","geoai_uos_neststateenum_operating"),
    DEBUG("DEBUG", "调试模式", "debug", "geoai_uos_neststateenum_debug"),
    BATTERY_REPLACING("BATTERY_REPLACING", "电池替换中", "battery replacing","geoai_uos_neststateenum_battery_replacing"),
    SYS_OPENED_WITHOUT_BAT("SYS_OPENED_WITHOUT_BAT", "未装载开启模式", "sys opened without bat", "geoai_uos_neststateenum_sys_opened_without_bat"),
    SYS_SUSPEND("SYS_SUSPEND", "系统挂起", "sys_suspend","geoai_uos_neststateenum_sys_suspend"),
    DRONE_POWER_ON_DOING("DRONE_POWER_ON_DOING", "无人机开关机进行中", "drone power on doing","geoai_uos_neststateenum_drone_power_on_doing"),

    DRONE_PAIRING_DOING("DRONE_PAIRING_DOING", "无人机对频进行中",  "drone pairing doing","geoai_uos_neststateenum_drone_pairing_doing"),

    BOOTLOADER_IDLE("BOOTLOADER_IDLE", "MPS固件更新初始化", "bootloader idle", "geoai_uos_neststateenum_bootloader_idle"),

    BOOTLOADER_WAITING_FOR_UPGRADE_CMD("BOOTLOADER_WAITING_FOR_UPGRADE_CMD", "MPS等待固件更新指令", "bootloader waiting for upgrade cmd","geoai_uos_neststateenum_bootloader_waiting_for_upgrade_cmd"),

    BOOTLOADER_PREPARE_TO_RECEIVE_FW("BOOTLOADER_PREPARE_TO_RECEIVE_FW", "MPS准备接收固件数据", "bootloader prepare to receive fw","geoai_uos_neststateenum_bootloader_prepare_to_receive_fw"),

    BOOTLOADER_RECEIVING_FM("BOOTLOADER_RECEIVING_FM", "MPS正在接收固件数据", "bootloader receiving fm", "geoai_uos_neststateenum_bootloader_receiving_fm"),

    BOOTLOADER_FINISH_RECEIVE_FW("BOOTLOADER_FINISH_RECEIVE_FW", "MPS固件数据接收完成", "bootloader finish receive fw", "geoai_uos_neststateenum_bootloader_finish_receive_fw"),

    BOOTLOADER_UPGRADING("BOOTLOADER_UPGRADING", "MPS正在更新固件", "bootloader upgrading", "geoai_uos_neststateenum_bootloader_upgrading"),

    BOOTLOADER_UPGRADED("BOOTLOADER_UPGRADED", "MPS固件更新完成", "bootloader upgraded", "geoai_uos_neststateenum_bootloader_upgraded"),

    BOOTLOADER_MAIN_PROGRAM_LAUNCHING("BOOTLOADER_MAIN_PROGRAM_LAUNCHING", "MPS正在启动", "bootloader main program launching", "geoai_uos_neststateenum_bootloader_main_program_launching"),

    BOOTLOADER_ENTERING_MPS_FW_UPGRADE_MODE("BOOTLOADER_ENTERING_MPS_FW_UPGRADE_MODE", "MPS正在进入固件更新模式", "bootloader entering mps fw upgrade mode", "geoai_uos_neststateenum_bootloader_entering_mps_fw_upgrade_mode"),

    BOOTLOADER_UPGRADE_ERR("BOOTLOADER_UPGRADE_ERR", "MPS固件更新异常", "bootloader upgrade err", "geoai_uos_neststateenum_bootloader_upgrade_err"),

    CONNECTING("CONNECTING", "MPS连接中", "connecting", "geoai_uos_neststateenum_connecting"),

    DISCONNECTED("DISCONNECTED", "MPS连接断开", "disconnected", "geoai_uos_neststateenum_disconnected"),

    UNKNOWN("UNKNOWN", "未知", "unknown", "geoai_uos_neststateenum_unknown"),

    DRONE_STARTED("DRONE_STARTED","无人机已开机","drone started","geoai_uos_neststateenum_drone_started"),

    OFF_LINE("OFF_LINE", "离线", "off line", "geoai_uos_neststateenum_off_line");

    private String value;
    /**
     * 0-固定机巢和迷你机巢共有，1-固定机巢独有，2-迷你机巢独有
     */
    private String chinese;

    private String english;

    private String key;

    NestStateEnum(String value, String chinese, String english, String key) {
        this.value = value;
        this.chinese = chinese;
        this.english = english;
        this.key = key;
    }

    public String getValue() {
        return value;
    }


    public String getChinese() {
        return chinese;
    }

    public String getEnglish() {
        return english;
    }

    public String getKey() {
        return key;
    }


    public static NestStateEnum getInstance(String value) {
        return valueOf(value);
    }

}
