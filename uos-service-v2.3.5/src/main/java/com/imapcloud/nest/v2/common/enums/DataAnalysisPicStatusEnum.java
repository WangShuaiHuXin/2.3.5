package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;
/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisPicStatusEnum.java
 * @Description DataAnalysisPicStatusEnum
 * @createTime 2022年07月08日 15:41:00
 */
public enum DataAnalysisPicStatusEnum implements ITypeEnum<DataAnalysisPicStatusEnum> {

    /**
     * 0：待分析
     */
    NEED_ANALYZE,

    /**
     * 1: 有问题
     */
    PROBLEM,

    /**
     * 2: 无问题
     */
    NO_PROBLEM;

    @Override
    public int getType() {
        return ordinal();
    }
}
