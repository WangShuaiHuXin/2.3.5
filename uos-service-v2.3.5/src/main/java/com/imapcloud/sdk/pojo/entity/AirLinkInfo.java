package com.imapcloud.sdk.pojo.entity;

public class AirLinkInfo {
    /**
     * 遥控器链路信号
     */
    private Integer wirelessLinkSignal = 0;
    /**
     * 4G链路信号
     */
    private Integer lteLinkSignal = 0;
    /**
     * 飞机通讯链路模式
     */
    private String airLinkMode = "UNKNOWN";
    /**
     * 遥控器链路是否连接
     */
    private Boolean isWirelessLinkConnected = false;
    /**
     * 4G链路是否连接
     */
    private Boolean isLteLinkConnected = false;

    public enum AirLinkModeEnum {
        WIRELESS_ONLY("WIRELESS_ONLY", "仅遥控器信"),
        LTE_ONLY("LTE_ONLY", "仅4G信号"),
        HYBRID_LTE_RC("HYBRID_LTE_RC", "4G和遥控器信号混合"),
        OTHER("OTHER", "其他"),
        UNKNOWN("UNKNOWN", "未知");
        private String val;
        private String chi;

        AirLinkModeEnum(String val, String chi) {
            this.val = val;
            this.chi = chi;
        }

        public static AirLinkModeEnum getInstance(String val) {
            for (AirLinkModeEnum e : AirLinkModeEnum.values()) {
                if (e.getVal().equals(val)) {
                    return e;
                }
            }
            return UNKNOWN;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        public String getChi() {
            return chi;
        }

        public void setChi(String chi) {
            this.chi = chi;
        }
    }

    public Integer getWirelessLinkSignal() {
        return wirelessLinkSignal;
    }

    public void setWirelessLinkSignal(Integer wirelessLinkSignal) {
        this.wirelessLinkSignal = wirelessLinkSignal;
    }

    public Integer getLteLinkSignal() {
        return lteLinkSignal;
    }

    public void setLteLinkSignal(Integer lteLinkSignal) {
        this.lteLinkSignal = lteLinkSignal;
    }

    public String getAirLinkMode() {
        return airLinkMode;
    }

    public void setAirLinkMode(String airLinkMode) {
        this.airLinkMode = airLinkMode;
    }

    public Boolean getIsWirelessLinkConnected() {
        return isWirelessLinkConnected;
    }

    public void setIsWirelessLinkConnected(Boolean isWirelessLinkConnected) {
        this.isWirelessLinkConnected = isWirelessLinkConnected;
    }

    public Boolean getIsLteLinkConnected() {
        return isLteLinkConnected;
    }

    public void setIsLteLinkConnected(Boolean isLteLinkConnected) {
        this.isLteLinkConnected = isLteLinkConnected;
    }
}
