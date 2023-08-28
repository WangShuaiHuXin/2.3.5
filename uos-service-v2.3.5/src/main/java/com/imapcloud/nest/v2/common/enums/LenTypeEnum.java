package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName LenTypeEnum.java
 * @Description LenTypeEnum
 * @createTime 2022年09月26日 15:41:00
 */
public enum LenTypeEnum implements ITypeEnum<LenTypeEnum> {

    /**
     * 0：普通可见光镜头
     */
    NORMAL_TYPE,

    /**
     * 1: 广角可见光镜头
     */
    WIDE_TYPE,

    /**
     * 2: 变焦可见光镜头
     */
    ZOOM_TYPE,

    /**
     * 3： 热红外镜头
     */
    THRM_TYPE;

    @Override
    public int getType() {
        return ordinal();
    }
}
