package com.imapcloud.nest.v2.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wmin
 */

public enum DJIOldHeadingTypeEnum {


    /**
     * 自动跟随
     */
    AUTO("Auto"),

    /**
     * 沿航点
     */
    USING_INITIAL_DIRECTION("UsePointSetting"),

    ;
    private String oldDjiHeading;

    DJIOldHeadingTypeEnum(String oldDjiHeading) {
        this.oldDjiHeading = oldDjiHeading;
    }

    public static String getEnumHeadingName(String oldDjiHeading){
        if(Objects.nonNull(oldDjiHeading)) {
            Optional<DJIOldHeadingTypeEnum> first = Arrays.stream(DJIOldHeadingTypeEnum.values())
                    .filter(e -> e.oldDjiHeading.equalsIgnoreCase(oldDjiHeading))
                    .findFirst();
            if(first.isPresent()) {
                return first.get().name();
            }
        }
        return null;
    }
}
