package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

/**
 * @author wmin
 */
@Data
public class LimitDistanceDO {
    /**
     * 0 - 未设置
     * 1 - 已经设置
     */
    private Integer state;

    /**
     * 限远距离
     * min - 15
     * max - 8000
     * 单位 米
     */
    private Integer distanceLimit;
}
