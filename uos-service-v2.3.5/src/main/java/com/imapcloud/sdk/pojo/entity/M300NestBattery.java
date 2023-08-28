package com.imapcloud.sdk.pojo.entity;

import com.geoai.common.web.util.MessageUtils;

/**
 * M300电池状态
 *
 * @Author: wmin
 * @Date: 2021/3/27 13:54
 */
public class M300NestBattery {

    public enum ChgStatEnum {
        /**
         * 电池充电状态
         */
        UNKNOWN(-1, "未知", "geoai_uos_ChgStatEnum_unknown"),
//        UNCHARGED(0, "未充电"),
//        READY_CHARGED(1, "准备充电"),
//        CHARGING(2, "充电中"),
//        CHARGED(3, "充电完成"),

        CHG_STAT_0(0, "停止充电", "geoai_uos_ChgStatEnum_chg_stat_0"),
        CHG_STAT_1(1, "准备充电", "geoai_uos_ChgStatEnum_chg_stat_1"),
        CHG_STAT_2(2, "预充电中", "geoai_uos_ChgStatEnum_chg_stat_2"),
        CHG_STAT_3(3, "恒流充电中", "geoai_uos_ChgStatEnum_chg_stat_3"),
        CHG_STAT_4(4, "恒压充电中", "geoai_uos_ChgStatEnum_chg_stat_4"),
        CHG_STAT_5(5, "充电中", "geoai_uos_ChgStatEnum_chg_stat_5"),
        CHG_STAT_6(6, "充电完成", "geoai_uos_ChgStatEnum_chg_stat_6"),

        ;
        private Integer value;
        private String express;

        private String key;
        public static ChgStatEnum getInstance(Integer value) {
            for (ChgStatEnum e : ChgStatEnum.values()) {
                if (e.getValue().equals(value)) {
                    return e;
                }
            }
            return UNKNOWN;
        }

        ChgStatEnum(Integer value, String express, String key) {
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

    }

    public enum RunStatEnum {
        /**
         * 运行状态
         */
        UNKNOWN(-1, "未知", "geoai_uos_RunStatEnum_unknown"),

        RUN_STAT_0(0,"物理连接和通讯连接断开", "geoai_uos_RunStatEnum_RUN_STAT_0"),
        RUN_STAT_1(1,"物理连接正常，通讯连接断开", "geoai_uos_RunStatEnum_RUN_STAT_1"),
        RUN_STAT_2(2,"物理连接断开，通讯连接正常", "geoai_uos_RunStatEnum_RUN_STAT_2"),
        RUN_STAT_3(3,"报警状态", "geoai_uos_RunStatEnum_RUN_STAT_3"),
        RUN_STAT_4(4,"故障状态", "geoai_uos_RunStatEnum_RUN_STAT_4"),
        RUN_STAT_5(5,"电池无异常", "geoai_uos_RunStatEnum_RUN_STAT_5"),
        ;
        private Integer value;
        private String express;

        private String key;

        public static RunStatEnum getInstance(Integer value) {
            for (RunStatEnum e : RunStatEnum.values()) {
                if (e.getValue().equals(value)) {
                    return e;
                }
            }
            return UNKNOWN;
        }

        RunStatEnum(Integer value, String express, String key) {
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
            return MessageUtils.getMessage(key);
        }

        public void setExpress(String express) {
            this.express = express;
        }
    }

    /**
     * 电池编号
     */
    private Integer number = -1;
    /**
     * 剩余电量
     */
    private Integer remainPercent = -1;
    /**
     * 充电状态
     * 0：未充电
     * 1：准备充电
     * 2：充电中
     * 3：充电完成
     */
    private ChgStatEnum chgStat = ChgStatEnum.UNKNOWN;

    private RunStatEnum runStat = RunStatEnum.UNKNOWN;

    /**
     * 电池是否有故障
     */
    private Boolean fault = false;

    /**
     * 电池循环次数
     */
    private Integer numOfDischarge = 0;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        if (number != null) {
            this.number = number;
        }
    }

    public Integer getRemainPercent() {
        return remainPercent;
    }

    public void setRemainPercent(Integer remainPercent) {
        if (remainPercent != null) {
            this.remainPercent = remainPercent;
        }
    }

    public ChgStatEnum getChgStat() {
        return chgStat;
    }

    public void setChgStat(Integer chgStat) {
        if (chgStat != null) {
            this.chgStat = ChgStatEnum.getInstance(chgStat);
        }
    }

    public Boolean getFault() {
        return fault;
    }

    public void setFault(Boolean fault) {
        this.fault = fault;
    }

    public RunStatEnum getRunStat() {
        return runStat;
    }

    public void setRunStat(Integer runStat) {
        if (runStat != null) {
            this.runStat = RunStatEnum.getInstance(runStat);
        }
    }

    public Integer getNumOfDischarge() {
        return numOfDischarge;
    }

    public void setNumOfDischarge(Integer numOfDischarge) {
        if (numOfDischarge != null) {
            this.numOfDischarge = numOfDischarge;
        }
    }
}
