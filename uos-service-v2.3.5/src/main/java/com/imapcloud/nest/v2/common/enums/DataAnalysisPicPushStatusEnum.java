package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;
/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisPicPushStatusEnum.java
 * @Description DataAnalysisPicPushStatusEnum
 * @createTime 2022年07月08日 15:41:00
 */
public enum DataAnalysisPicPushStatusEnum implements ITypeEnum<DataAnalysisPicPushStatusEnum> {

    /**
     * 0：提交态
     */
    COMMIT,

    /**
     * 1: 核实态
     */
    VERIFY;

    @Override
    public int getType() {
        return ordinal();
    }
}
