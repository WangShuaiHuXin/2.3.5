package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 基站国标设备用途
 * @author Vastfy
 * @date 2022/12/02 14:34
 * @since 2.1.5
 */
public enum NestDeviceUseEnum implements ITypeEnum<NestDeviceUseEnum> {

    /**
     * 1：巢内
     */
    INNER,

    /**
     * 2：巢外
     */
    OUTER,

    ;

    @Override
    public int getType() {
        return ordinal();
    }

    public static Optional<NestDeviceUseEnum> findMatch(int type){
        return Arrays.stream(NestDeviceUseEnum.values())
                .filter(e -> Objects.equals(type, e.getType()))
                .findFirst();
    }

}
