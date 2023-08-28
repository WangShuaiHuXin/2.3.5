package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;

/**
 * 识别数据类型
 */
public enum RecDataTypeEnum implements ITypeEnum<RecDataTypeEnum> {
    /**
     * 全部：-1
     */
    ALL,

    /**
     * 图片：0
     */
    IMAGE,

    /**
     * 流媒体：1
     */
    STREAMING;

    @Override
    public int getType() {
        return ordinal() - 1;
    }
}
