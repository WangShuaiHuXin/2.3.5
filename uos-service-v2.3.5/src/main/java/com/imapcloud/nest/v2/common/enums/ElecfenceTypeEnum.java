package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;

/**
 * 电子围栏类型
 * @author Vastfy
 * @date 2022/10/21 15:14
 * @since 1.9.7
 */
public enum ElecfenceTypeEnum implements ITypeEnum<ElecfenceTypeEnum> {

    /**
     * 1：适航区
     */
    AIRWORTHINESS_ZONE,

    /**
     * 2: 禁飞区
     */
    NO_FLY_ZONE,
    ;

    @Override
    public int getType() {
        return ordinal() + 1;
    }

}
