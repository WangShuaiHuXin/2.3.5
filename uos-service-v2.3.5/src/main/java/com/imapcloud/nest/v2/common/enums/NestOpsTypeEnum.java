package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;

/**
 * 基站操作类型
 * @author Vastfy
 * @date 2022/5/24 17:34
 * @since 1.0.0
 */
public enum NestOpsTypeEnum implements ITypeEnum<NestOpsTypeEnum> {

    /**
     * 0：可见
     */
    VISIBLE,

    /**
     * 1: 可操作
     */
    OPERABLE
    ;

    @Override
    public int getType() {
        return ordinal();
    }

}
