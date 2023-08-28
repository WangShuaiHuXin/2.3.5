package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 * RTK连接类型
 */

public enum RtkConnTypeEnum {
    /**
     * rtk类型（0-未知；1-网咯RTK；2-自定义网络RTK；3-D-RTK基站 ）
     */
    UNKNOWN(0),
    NETWORK(1),
    CUSTOM_NETWORK(2),
    BASE_STATION(3);
    private Integer value;

    RtkConnTypeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static RtkConnTypeEnum getInstance(Integer value) {
        for (RtkConnTypeEnum e : RtkConnTypeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
