package com.imapcloud.sdk.manager.general.enums;

public enum PushStreamMode {
    /**
     * 未知
     */
    UNKNOWN(-1),

    /**
     * 软件推流
     */
    SOFT_PUSH(0),
    /**
     * HDMI硬件推流（需基站安装HDMI推流硬件，针对型号1）
     */
    HDMI_PUSH1(1),

    /**
     * 云冠推流（需无人机搭载云冠设备）
     */
    ICREST_PUSH(2),

    /**
     * HDMI硬件推流（需基站安装HDMI推流硬件，针对型号2）
     */
    HDMI_PUSH2(3),

    /**
     * HDMI硬件推流（需基站安装HDMI推流硬件，针对型号3 V 2.3.0以上版本）
     */
    HDMI_PUSH3(4),
    /**
     * 自研推流模块(V2.3.0及以上版本)
     */
    SELF_DEVELOPED_PUSH(5),
    ;
    private int value;

    PushStreamMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PushStreamMode getInstance(Integer value) {
        if (value == null) {
            return null;
        }

        for (PushStreamMode e : PushStreamMode.values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return null;
    }
}
