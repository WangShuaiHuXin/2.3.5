package com.imapcloud.sdk.pojo.entity;

import java.util.List;

/**
 * 图传信道
 */
public class ChannelInterferenceState {
    /**
     * 信道干扰强度平均值,单位dBm
     */
    private Double avgFrequencyInterference;
    /**
     * 信道下行带宽，MHz
     */
    private Double channelBandwidth;
    /**
     * 当前信道频率,Hz
     */
    private Double channelNumber;
    /**
     * 信道选择模式,AUTO,MANUAL,UNKNOW
     */
    private String channelSelectionMode;
    /**
     * 信道频率类型,FREQUENCY_BAND_DUAL,
     * FREQUENCY_BAND_2_DOT_4_GHZ,
     * FREQUENCY_BAND_5_DOT_8_GHZ,
     * FREQUENCY_BAND_5_DOT_7_GHZ,
     * FREQUENCY_BAND_840M_1_DOT_4_GHZ,
     * UNKNOWN
     */
    private String frequencyBand;
    /**
     * 图传码率,Mbps
     */
    private Double videoDataRate;

    /**
     * 各频率区间干扰信息
     */
    private List<FiItem> frequencyInterference;

    /**
     * 图传信号状态,GOOD（信号良好）,
     * MODERATE（信号一般）,
     * WEAK（信号微弱）,
     * UNKNOWN（状态未知）
     */
    private String signalState;

    public static class FiItem{
        /**
         * 频率区间起点,Hz
         */
        private Double from;
        /**
         * 频率区间终点,Hz
         */
        private Double to;
        /**
         * 当前区间信号干扰强度,dBm
         */
        private Double rssi;

        public Double getFrom() {
            return from;
        }

        public void setFrom(Double from) {
            this.from = from;
        }

        public Double getTo() {
            return to;
        }

        public void setTo(Double to) {
            this.to = to;
        }

        public Double getRssi() {
            return rssi;
        }

        public void setRssi(Double rssi) {
            this.rssi = rssi;
        }
    }

    public Double getAvgFrequencyInterference() {
        return this.avgFrequencyInterference;
    }
    public void setAvgFrequencyInterference(Double avgFrequencyInterference) {
        this.avgFrequencyInterference = avgFrequencyInterference;
    }
    public String getSignalState() {
        return this.signalState;
    }
    public void setSignalState(String signalState) {
        this.signalState = signalState;
    }
}
