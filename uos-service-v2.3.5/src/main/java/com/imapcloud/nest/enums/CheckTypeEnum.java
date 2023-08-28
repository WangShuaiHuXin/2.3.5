package com.imapcloud.nest.enums;

/**
 * 检查类型枚举
 *
 * @author daolin
 */
public enum CheckTypeEnum {
    /**
     * 机巢检查
     */
    NEST_CHECK,
    /**
     * 气象检查
     */
    AEROGRAPH_CHECK,
    /**
     * 电池检查
     */
    BATTERY_CHECK,
    /**
     * 天线检查
     */
    ANTENNA_CHECK,
    /**
     * 电池装载
     */
    BATTERY_LOADING,

    /**
     * 电池装载完毕
     */
    BATTERY_LOADED,
    /**
     * 准备阶段
     */
    PREPARING,
    /**
     * 舱门打开
     */
    CABIN_OPENING,
    /**
     * 平台升起
     */
    LIFT_RISING,
    /**
     * 归中释放
     */
    SQUARE_Y,

    /**
     * 固定解
     */
    FIXED_POINT,

    /**
     * 任务上传中
     */
    MISSION_UPLOADING,
    /**
     * 飞行前检查
     */
    BEFORE_FLY_CHECK,
    /**
     * 飞机起飞
     */
    TAKE_OFF,

    /**
     * 启动过程结束
     */
    FINISH,
    /**
     * 上传航线
     */
    UPLOAD_AIRLINE,
    /**
     * 飞行中
     */
    EXECUTING,
    /**
     * 返航
     */
    BACKING,
    /**
     * 降落
     */
    LANDING,
    /**
     * 回收
     */
    RECOVERYING,
    /**
     * 下载图片
     */
    DOWNLOADING_PHOTO,
    /**
     * 卸载电池
     */
    BATTERY_UNLOADING,
    /**
     * 准备就绪
     */
    STANDBY,

    AIRCRAFT_POWER_ON
    ;


}
