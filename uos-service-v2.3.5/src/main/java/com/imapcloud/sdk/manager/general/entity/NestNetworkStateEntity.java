package com.imapcloud.sdk.manager.general.entity;

public class NestNetworkStateEntity {
    /**
     * Ping 操作目标主机
     */
    private String host = "";
    /**
     * Ping操作回包数
     */
    private Integer receivedPackage = 0;
    /**
     *
     */
    private Integer packageSize = 0;
    /**
     * Ping操作总耗时
     */
    private String timeSpent = "";
    /**
     * 平均下载网速
     */
    private String avgSpeed = "";
    /**
     * Ping操作发包数
     */
    private Integer transmittedPackage = 0;
    /**
     * 平均TTL
     */
    private String avgTTL = "";
    /**
     * 最大TTL
     */
    private String maxTTL = "";
    /**
     * 最小TTL
     */
    private String minTTL = "";
    /**
     * Ping操作丢包率
     */
    private String lossPercent = "";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        if(host != null) {
            this.host = host;
        }
    }

    public Integer getReceivedPackage() {
        return receivedPackage;
    }

    public void setReceivedPackage(Integer receivedPackage) {
        if(receivedPackage != null) {
            this.receivedPackage = receivedPackage;
        }
    }

    public Integer getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(Integer packageSize) {
        if(packageSize != null) {
            this.packageSize = packageSize;
        }
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        if(timeSpent != null) {
            this.timeSpent = timeSpent;
        }
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        if(avgSpeed != null) {
            this.avgSpeed = avgSpeed;
        }
    }

    public Integer getTransmittedPackage() {
        return transmittedPackage;
    }

    public void setTransmittedPackage(Integer transmittedPackage) {
        if(transmittedPackage != null) {
            this.transmittedPackage = transmittedPackage;
        }
    }

    public String getAvgTTL() {
        return avgTTL;
    }

    public void setAvgTTL(String avgTTL) {
        if(avgTTL != null) {
            this.avgTTL = avgTTL;
        }
    }

    public String getMaxTTL() {
        return maxTTL;
    }

    public void setMaxTTL(String maxTTL) {
        if(maxTTL != null) {
            this.maxTTL = maxTTL;
        }
    }

    public String getMinTTL() {
        return minTTL;
    }

    public void setMinTTL(String minTTL) {
        if(minTTL != null) {
            this.minTTL = minTTL;
        }
    }

    public String getLossPercent() {
        return lossPercent;
    }

    public void setLossPercent(String lossPercent) {
        if(lossPercent != null) {
            this.lossPercent = lossPercent;
        }
    }
}
