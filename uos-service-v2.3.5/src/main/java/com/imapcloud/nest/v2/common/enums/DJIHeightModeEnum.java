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
public enum DJIHeightModeEnum {

    /**
     * 绝对高度
     */
    EGM96("EGM96","绝对高度"),

    /**
     * 执行绝对高度模式
     */
    WGS84("WGS84" ,"WGS84"),

    /**
     * 相对起飞点
     */
    RELATIVE_TO_STARTPOINT("relativeToStartPoint","相对起飞点"),

    /**
     * 相对基站
     */
    ABOVE_GROUND_LEVEL("aboveGroundLevel","相对基站"),

    /**
     * 实时仿地模式
     */
    REALTIME_FOLLOW_SURFACE("realTimeFollowSurface" , "实时仿地模式");

    ;

    private String code;

    private String des;

    DJIHeightModeEnum(String code , String des){
        this.code = code;
        this.des = des;
    }

    public static Optional<DJIHeightModeEnum> findMatch(String code){
        return Arrays.stream(DJIHeightModeEnum.values())
                .filter(e -> Objects.equals(code, e.getCode()))
                .findFirst();
    }

}
