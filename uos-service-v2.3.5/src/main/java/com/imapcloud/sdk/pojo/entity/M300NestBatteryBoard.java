package com.imapcloud.sdk.pojo.entity;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 电池板
 *
 * @Author: wmin
 * @Date: 2021/3/27 13:47
 */
public class M300NestBatteryBoard {
    /**
     * 电池组信息
     */
    private List<M300NestBatteries> batteryGroups = Collections.emptyList();
    /**
     * 层编号
     */
    private Integer layer = -1;
    /**
     * 电源位
     */
    private List<Boolean> smpsStatesFault = Collections.emptyList();

    private List<SmpsStatesEnum> smpsStates = Collections.emptyList();
    /**
     * 电池板状态
     */
    private Integer status = -1;
    /**
     * 电池板版本
     */
    private Integer version = -1;

    public List<M300NestBatteries> getBatteryGroups() {
        return batteryGroups;
    }

    public void setBatteryGroups(List<M300NestBatteries> batteryGroups) {
        if (batteryGroups != null) {
            this.batteryGroups = batteryGroups;
        }
    }

    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        if (layer != null) {
            this.layer = layer;
        }
    }

    public List<Boolean> getSmpsStatesFault() {
        return smpsStatesFault;
    }

    public void setSmpsStatesFault(List<Boolean> smpsStatesFault) {
        if (smpsStatesFault != null) {
            this.smpsStatesFault = smpsStatesFault;
        }
    }

    public List<SmpsStatesEnum> getSmpsStates() {
        return smpsStates;
    }

    public void setSmpsStates(List<Integer> smpsStates) {
        this.smpsStates = SmpsStatesEnum.listInstance(smpsStates);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        if (status != null) {
            this.status = status;
        }
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        if (version != null) {
            this.version = version;
        }
    }

    public enum SmpsStatesEnum {
        UNKNOWN(-1, "未知"),
        SMPS_STATES_0(0, "物理连接和通讯连接断开"),
        SMPS_STATES_1(1, "物理连接正常，通讯连接断开"),
        SMPS_STATES_2(2, "物理连接断开，通讯连接正常"),
        SMPS_STATES_3(3, "报警状态"),
        SMPS_STATES_4(4, "故障状态"),
        SMPS_STATES_5(5, "开关电源无异常"),

        ;
        private Integer value;
        private String express;

        public static SmpsStatesEnum getInstance(Integer value) {
            for (SmpsStatesEnum e : SmpsStatesEnum.values()) {
                if (e.getValue().equals(value)) {
                    return e;
                }
            }
            return UNKNOWN;
        }

        public static List<SmpsStatesEnum> listInstance(List<Integer> values) {
            if (CollectionUtil.isNotEmpty(values)) {
                return Arrays.stream(SmpsStatesEnum.values()).filter(e -> values.contains(e.getValue())).collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        SmpsStatesEnum(Integer value, String express) {
            this.value = value;
            this.express = express;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getExpress() {
            return express;
        }

        public void setExpress(String express) {
            this.express = express;
        }
    }
}
