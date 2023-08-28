package com.imapcloud.nest.v2.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJICameraEnum.java
 * @Description DJICameraEnum
 * @createTime 2022年07月08日 15:41:00
 */
@Getter
public enum DJIFlyToWaylineModeEnum {

    /**
     * 安全模式
     */
    SAFELY("safely","安全模式"),

    /**
     * 倾斜飞行模式
     */
    POINT_TO_POINT("pointToPoint","倾斜飞行模式"),

    ;

    private String code;

    private String des;

    DJIFlyToWaylineModeEnum(String code , String des){
        this.code = code;
        this.des = des;
    }

    public static Optional<DJIFlyToWaylineModeEnum> findMatch(String code){
        return Arrays.stream(DJIFlyToWaylineModeEnum.values())
                .filter(e -> Objects.equals(code, e.getCode()))
                .findFirst();
    }

}
