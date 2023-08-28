package com.imapcloud.nest.v2.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIActionTypeEnum.java
 * @Description DJIActionTypeEnum
 * @createTime 2022年07月08日 15:41:00
 * takePhoto：单拍
 *                           startRecord：开始录像
 *                           stopRecord：结束录像
 *                           focus：对焦
 *                           zoom：变焦
 *                           customDirName：创建新文件夹
 *                           gimbalRotate：旋转云台
 *                           rotateYaw：飞行器偏航
 *                           hover：悬停等待
 */
@Getter
public enum DJIActionTypeEnum {

    /**
     * -1：单拍
     */
    TAKE_PHOTO(-1,"takePhoto","START_TAKE_PHOTO"),

    /**
     * 0：开始录像
     */
    START_RECORD(0,"startRecord","START_RECORD"),

    /**
     * 1: 结束录像
     */
    STOP_RECORD(1,"stopRecord","STOP_RECORD"),

    /**
     * 2: 对焦
     */
    FOCUS(2,"focus","FOCUS"),

    /**
     * 3: 变焦
     */
    ZOOM(3,"zoom","CAMERA_ZOOM"),

    /**
     * 4: 创建新文件夹
     */
    CUSTOM_DIR_NAME(4,"customDirName","CUSTOM_DIR_NAME"),

    /**
     * 旋转云台 -uos只有俯仰，没有云台偏航
     */
    GIMBAL_ROTATE(5,"gimbalRotate","GIMBAL_PITCH"),

    /**
     * 飞行器偏航
     */
    ROTATE_YAW(6,"rotateYaw","ROTATE_AIRCRAFT"),

    /**
     * 悬停等待
     */
    HOVER(7,"hover","STAY"),

    /**
     * 精准复拍（旧版）
     */
    ACCURATE_SHOOT(8,"accurateShoot","ACCURATE_SHOOT"),


    /**
     * 精准复拍（新版）
     */
    ORIENTED_SHOOT(9,"orientedShoot","ORIENTED_SHOOT"),


    ;

    private int code;

    private String djiActionType;

    private String actionType;

    DJIActionTypeEnum(int code, String djiActionType , String actionType){
        this.code = code;
        this.djiActionType = djiActionType;
        this.actionType = actionType;
    }

    public static Optional<DJIActionTypeEnum> findMatchActionType(String djiActionType){
        return Arrays.stream(DJIActionTypeEnum.values())
                .filter(e -> Objects.equals(djiActionType, e.getDjiActionType()))
                .findFirst();
    }


    public static Optional<DJIActionTypeEnum> findMatchDjiActionType(String actionType){
        return Arrays.stream(DJIActionTypeEnum.values())
                .filter(e -> Objects.equals(actionType,e.getActionType()))
                .findFirst();
    }


    public static Optional<DJIActionTypeEnum> findMatch(int code){
        return Arrays.stream(DJIActionTypeEnum.values())
                .filter(e -> Objects.equals(code, e.getCode()))
                .findFirst();
    }
}
