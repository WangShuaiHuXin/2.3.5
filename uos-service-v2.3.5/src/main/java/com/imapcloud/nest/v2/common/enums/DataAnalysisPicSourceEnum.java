package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisPicSourceEnum.java
 * @Description DataAnalysisPicSourceEnum
 * @createTime 2022年07月08日 15:41:00
 */
public enum DataAnalysisPicSourceEnum implements ITypeEnum<DataAnalysisPicSourceEnum> {

    /**
     * 0：现场取证
     */
    DATA_SCENE,

    /**
     * 1: 照片
     */
    PHOTO,

    /**
     * 2: 视频
     */
    VIDEO;

    @Override
    public int getType() {
        return ordinal();
    }
}
