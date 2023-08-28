package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointType.java
 * @Description DataPanoramaPointType
 * @createTime 2022年09月27日 18:17:00
 */
public enum DataPanoramaPointTypeEnum implements ITypeEnum<LenTypeEnum> {

    /**
     * 0：自动
     */
    AUTO_TYPE,

    /**
     * 1: 手工
     */
    MANUAL_TYPE;


    @Override
    public int getType() {
        return ordinal();
    }
}