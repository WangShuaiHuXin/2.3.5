package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;
/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisMarkStatusEnum.java
 * @Description DataAnalysisMarkStatusEnum
 * @createTime 2022年07月08日 15:41:00
 */
public enum DataAnalysisMarkStatusEnum implements ITypeEnum<DataAnalysisMarkStatusEnum> {

    /**
     * 0：未核实
     */
    NOT_CONFIRM,

    /**
     * 1: 已核实
     */
    CONFIRM;

    @Override
    public int getType() {
        return ordinal();
    }
}
