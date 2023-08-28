package com.imapcloud.nest.v2.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIHeadingTypeEnum.java
 * @Description DJIHeadingTypeEnum
 * @createTime 2022年07月08日 15:41:00
 */
@Getter
public enum DJIHeadingTypeEnum {


    /**
     * -1：followWayline 沿航线方向
     */
    AUTO(-1,"followWayline","AUTO"),

    /**
     * 0：fixed 锁定当前偏航角
     */
    USING_INITIAL_DIRECTION(0,"fixed","USING_INITIAL_DIRECTION"),

    /**
     * 1: smoothTransition 平滑过度
     */
    USING_WAYPOINT_HEADING(1,"smoothTransition","USING_WAYPOINT_HEADING");

    private int code;

    private String djiHeadingType;

    private String headingType;

    DJIHeadingTypeEnum(int code, String djiHeadingType , String headingType){
        this.code = code;
        this.djiHeadingType = djiHeadingType;
        this.headingType = headingType;
    }

    public static Optional<DJIHeadingTypeEnum> findMatchActionType(String djiHeadingType){
        return Arrays.stream(DJIHeadingTypeEnum.values())
                .filter(e -> Objects.equals(djiHeadingType, e.getDjiHeadingType()))
                .findFirst();
    }

    public static Optional<DJIHeadingTypeEnum> findMatchDjiActionType(String headingType){
        return Arrays.stream(DJIHeadingTypeEnum.values())
                .filter(e -> Objects.equals(headingType,e.getHeadingType()))
                .findFirst();
    }


    public static Optional<DJIHeadingTypeEnum> findMatch(int code){
        return Arrays.stream(DJIHeadingTypeEnum.values())
                .filter(e -> Objects.equals(code, e.getCode()))
                .findFirst();
    }
}
