package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: wmin
 * @Date: 2021/4/15 11:37
 */
@Data
public class AroundFlyParam {
    /**
     * 任务模式
     * VIDEO_CAPTURE
     * CAMERA_TIMER
     */
    private TaskMode taskMode;
    /**
     * 飞行速度
     */
    private Integer speed;

    /**
     * 云台俯仰
     */
    private Integer gimbalPitch;

    /**
     * 起降航高，单位m
     */
    private Integer takeOffLandAlt;

    /**
     * 最小航线高度，单位
     */
    private Integer minLineAlt;

    /**
     * 最大航高，单位m
     */
    private Integer maxLineAlt;

    /**
     * 飞行层数
     */
    private Integer flyLayerNum;

    /**
     * 定时拍照时间间隔
     */
    private Float shootPhotoTimeInterval;

    /**
     * 定距拍照间隔
     */
    private Float shootPhotoDistanceInterval;

    /**
     * 环绕中心点
     * lat -> 纬度
     * lng -> 经度
     * alt -> 高度
     */
    private Map<String, Double> centerPoint;

    /**
     * 入口点
     * lat -> 纬度
     * lng -> 经度
     * alt -> 高度
     */
    private Map<String, Double> entryPoint;

    /**
     * 航线航点别名
     */
    private String byname;

    /**
     * 识别类型
     */
    private List<Integer> photoPropList;

    public enum TaskMode {
        VIDEO_CAPTURE, CAMERA_TIMER, CAMERA_DISTANCE
    }

}
