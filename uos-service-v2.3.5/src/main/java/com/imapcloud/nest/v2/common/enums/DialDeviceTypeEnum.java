package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.IStatusEnum;

/**
 * 表计设备状态
 * @author Vastfy
 * @date 2022/12/04 17:34
 * @since 2.1.5
 */
public enum DialDeviceTypeEnum implements IStatusEnum<DialDeviceTypeEnum> {

    /**
     * 0：未知
     */
    UNKNOWN,

    /**
     * 1：正常
     */
    NORMAL,

    /**
     * 2: 一般缺陷
     */
    ABNORMAL_ALARM,

    /**
     * 3: 严重缺陷
     */
    DEFECT_ALARM,

    ;

    @Override
    public int getStatus() {
        return ordinal();
    }
}
