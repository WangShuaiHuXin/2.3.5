package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;

/**
 * 表计设备状态
 * @author Vastfy
 * @date 2022/12/04 17:34
 * @since 2.1.5
 */
public enum DialAlarmReasonTypeEnum implements ITypeEnum<DialAlarmReasonTypeEnum> {

    /**
     * -1；无告警原因
     */
    NON,

    /**
     * 0：存在读数异常：存在读数信息，且任意一个读数项不满足读数规则
     */
    ABNORMAL_READING,

    /**
     * 1：未能识别读数：已进行AI识别，未获取读数信息/所识别读数不满足读数项范围
     */
    READING_NOT_RECOGNIZED,

    /**
     * 2: 未设置读数项：已进行AI识别，但该巡检点照片无部件关联，不存在读数项
     */
    READING_ITEM_NOT_SET,

    ;

    @Override
    public int getType() {
        return ordinal() - 1;
    }
}
