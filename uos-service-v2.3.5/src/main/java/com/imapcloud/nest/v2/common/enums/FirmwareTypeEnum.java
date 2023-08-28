package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 固件类型
 * @author Vastfy
 * @date 2022/07/13 11:54
 * @since 1.0.0
 */
public enum FirmwareTypeEnum implements ITypeEnum<FirmwareTypeEnum> {

    /**
     * 1：CPS
     */
    CPS,

    /**
     * 2: MPS
     */
    MPS
    ;

    @Override
    public int getType() {
        return ordinal() + 1;
    }

    public static Optional<FirmwareTypeEnum> findMatch(int type){
        return Arrays.stream(FirmwareTypeEnum.values())
                .filter(e -> Objects.equals(type, e.getType()))
                .findFirst();
    }

    public static Optional<FirmwareTypeEnum> findMatch(String name){
        if(StringUtils.hasText(name)){
            return Arrays.stream(FirmwareTypeEnum.values())
                    .filter(e -> Objects.equals(name.toUpperCase(), e.name()))
                    .findFirst();
        }
        return Optional.empty();
    }

}
