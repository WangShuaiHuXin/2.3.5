package com.imapcloud.sdk.pojo.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public enum MissionQueueEnum {
    STATUS_BASE("/status/base"),
    STATUS_BASE1("/status/base/1"),
    STATUS_BASE2("/status/base/2"),
    STATUS_BASE3("/status/base/3"),
    STATUS_MISSION("/status/mission"),
    STATUS_MISSION1("/status/mission/1"),
    STATUS_MISSION2("/status/mission/2"),
    STATUS_MISSION3("/status/mission/3"),
    UNKNOWN("");
    private String value;

    MissionQueueEnum(String value) {
        this.value = value;
    }

    public static MissionQueueEnum getInstance(String value) {
        if (value != null) {
            for (MissionQueueEnum e : MissionQueueEnum.values()) {
                if (e.value.equals(value)) {
                    return e;
                }
            }
        }
        return UNKNOWN;
    }

    public String getValue() {
        return value;
    }

    public static boolean contains(String topic){
        if(Objects.nonNull(topic)) {
            for (MissionQueueEnum e : MissionQueueEnum.values()) {
                if (e.value.equals(topic)) {
                    return true;
                }
            }
        }
        return false;
    }
}
