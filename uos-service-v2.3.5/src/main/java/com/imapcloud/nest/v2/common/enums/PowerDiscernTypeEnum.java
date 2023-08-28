package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;

/**
 * 电力-识别类型
 * @author Vastfy
 * @date 2022/12/02 14:34
 * @since 2.1.5
 */
public enum PowerDiscernTypeEnum implements ITypeEnum<PowerDiscernTypeEnum> {

    /**
     * 1：缺陷识别
     */
    DEFECTS,

    /**
     * 2: 表计读数
     */
    DIALS,

    /**
     * 3: 红外测温
     */
    INFRARED,

    ;

    @Override
    public int getType() {
        return ordinal() + 1;
    }
}
