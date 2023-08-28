package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.IStatusEnum;

/**
 * 表计读数状态
 * @author Vastfy
 * @date 2022/12/04 17:34
 * @since 2.1.5
 */
public enum DialReadingTypeEnum implements IStatusEnum<DialReadingTypeEnum> {

    /**
     * 0：未处理
     */
    UNTREATED,

    /**
     * 1：识别中
     */
    RECOGNIZING,

    /**
     * 2: 有读数
     */
    WITH_READING,

    /**
     * 3: 无读数
     */
    NO_READING,

    ;

    @Override
    public int getStatus() {
        return ordinal();
    }
}
