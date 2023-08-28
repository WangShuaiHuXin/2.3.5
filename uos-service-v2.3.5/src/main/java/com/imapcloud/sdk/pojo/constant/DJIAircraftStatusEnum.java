package com.imapcloud.sdk.pojo.constant;

import com.geoai.common.core.enums.ITypeEnum;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIAircraftStatusEnum.java
 * @Description DJIAircraftStatusEnum
 * @createTime 2022年07月08日 15:41:00
 */
public enum DJIAircraftStatusEnum implements ITypeEnum<DJIAircraftStatusEnum> {

    /**
     * 0：待机
     */
    STAND_BY,

    /**
     * 1: 起飞准备
     */
    FLY_READY,

    /**
     * 2: 起飞准备完毕
     */
    FLY_READY_OVER,

    /**
     * 3:手动飞行
     */
    MANUAL_FLIGHT,

    /**
     * 4:自动飞行
     */
    AUTO_FLY,

    /**
     * 5:航线飞行
     */
    AIR_LINE_FLY,

    /**
     * 6：全景拍照
     */
    PANORAMA,

    /**
     * 7：智能跟随
     */
    INTELLIGENT_FOLLOW,

    /**
     * ADS_B躲避
     */
    ADS_B_HIDE,

    /**
     * 自动返航
     */
    AUTOMATIC_RETURN,

    /**
     * 自动降落
     */
    AUTOMATIC_LANDING,

    /**
     * 强制降落
     */
    FORCED_LANDING,

    /**
     * 三桨叶降落
     */
    THREE_BLADES_FALLING,

    /**
     * 升级中
     */
    UPGRADING,

    /**
     * 未连接
     */
    DISCONNECTED
    ;



    @Override
    public int getType() {
        return ordinal();
    }
}
