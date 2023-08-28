package com.imapcloud.sdk.pojo.entity;

import com.geoai.common.web.util.MessageUtils;

import java.util.Collections;
import java.util.List;

/**
 * M300电池组
 *
 * @Author: wmin
 * @Date: 2021/3/27 14:15
 */
public class M300NestBatteries {

    public enum batteriesStatusEnum {
        /**
         * 电池组状态
         */
        UNKNOWN(-1, "获取不到状态", "geoai_uos_batteriesStatusEnum_unknown"),
        BATTERY_GROUP_STATE_0(0, "电池组已充满", "geoai_uos_batteriesStatusEnum_battery_group_state_0"),
        BATTERY_GROUP_STATE_1(1, "电池组电量不满", "geoai_uos_batteriesStatusEnum_battery_group_state_1"),
        BATTERY_GROUP_STATE_2(2, "电池组电压不平衡", "geoai_uos_batteriesStatusEnum_battery_group_state_2"),
        BATTERY_GROUP_STATE_3(3, "电池组准备充电中", "geoai_uos_batteriesStatusEnum_battery_group_state_3"),
        BATTERY_GROUP_STATE_4(4, "电池组充电中", "geoai_uos_batteriesStatusEnum_battery_group_state_4"),
        BATTERY_GROUP_STATE_5(5, "电池组停止充电", "geoai_uos_batteriesStatusEnum_battery_group_state_5"),
        BATTERY_GROUP_STATE_32(32, "电池使用中", "geoai_uos_batteriesStatusEnum_battery_group_state_32"),
        BATTERY_GROUP_STATE_33(33, "电池缺失", "geoai_uos_batteriesStatusEnum_battery_group_state_33"),
        BATTERY_GROUP_STATE_34(34, "电池组连接错误", "geoai_uos_batteriesStatusEnum_battery_group_state_34"),
        BATTERY_GROUP_STATE_35(35, "电池组通讯错误", "geoai_uos_batteriesStatusEnum_battery_group_state_35"),
        BATTERY_GROUP_STATE_48(48, "电池组报警", "geoai_uos_batteriesStatusEnum_battery_group_state_48"),
        BATTERY_GROUP_STATE_55(55, "电池组故障","geoai_uos_batteriesStatusEnum_battery_group_state_55"),
        BATTERY_GROUP_STATE_255(255, "未知", "geoai_uos_batteriesStatusEnum_battery_group_state_255"),

        ;
        private Integer value;
        private String express;

        private String key;

        batteriesStatusEnum(Integer value, String express, String key) {
            this.value = value;
            this.express = express;
            this.key = key;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getExpress() {
            return key;
        }

        public void setExpress(String express) {
            this.express = express;
        }

        public static batteriesStatusEnum getInstance(Integer value) {
            for (batteriesStatusEnum e : batteriesStatusEnum.values()) {
                if (e.getValue().equals(value)) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * 电池信息
     */
    private List<M300NestBattery> batteries = Collections.emptyList();
    /**
     * 电池组编号
     */
    private Integer groupId = -1;
    /**
     * 电池组停用、启用
     */
    private Boolean enable = true;

    private batteriesStatusEnum status = batteriesStatusEnum.UNKNOWN;

    public List<M300NestBattery> getBatteries() {
        return batteries;
    }

    public void setBatteries(List<M300NestBattery> batteries) {
        if (batteries != null) {
            this.batteries = batteries;
        }
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        if (groupId != null) {
            this.groupId = groupId;
        }
    }

    public batteriesStatusEnum getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        if (status != null) {
            this.status = batteriesStatusEnum.getInstance(status);
        }
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
