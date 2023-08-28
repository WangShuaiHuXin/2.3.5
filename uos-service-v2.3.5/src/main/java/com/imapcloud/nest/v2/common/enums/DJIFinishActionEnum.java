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
public enum DJIFinishActionEnum {

    /**
     * 飞行器完成航线任务后，退出航线模式并返航
     */
    GO_HOME("goHome","飞行器完成航线任务后，退出航线模式并返航"),

    /**
     * 飞行器完成航线任务后，退出航线模式
     */
    NO_ACTION("noAction","飞行器完成航线任务后，退出航线模式"),

    /**
     * 飞行器完成航线任务后，退出航线模式并原地降落
     */
    AUTO_LAND("autoLand","飞行器完成航线任务后，退出航线模式并原地降落"),

    /**
     * 飞行器完成航线任务后，立即飞向航线起始点，到达后退出航线模式。
     */
    GOTO_FIRST_WAYPOINT("gotoFirstWaypoint" , "飞行器完成航线任务后，立即飞向航线起始点，到达后退出航线模式。");

    ;

    private String code;

    private String des;

    DJIFinishActionEnum(String code , String des){
        this.code = code;
        this.des = des;
    }

    public static Optional<DJIFinishActionEnum> findMatch(String code){
        return Arrays.stream(DJIFinishActionEnum.values())
                .filter(e -> Objects.equals(code, e.getCode()))
                .findFirst();
    }

}
