package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import lombok.Data;

import java.util.List;

/**
 * Created by wmin on 2020/11/17 9:51
 *
 * @author wmin
 */
@Data
public class SlopePhotoParam {
    /**
     * 分辨率,单位cm
     */
    private Double resolution;
    /**
     * 速度,单位m/s
     */
    private Integer speed;
    /**
     * 旁向重叠度
     */
    private Double sideOverlap;
    /**
     * 航线重叠度
     */
    private Double routeOverlap;

    /**
     * 航线高度,单位m
     */
    private Integer lineAlt;

    /**
     * 起降航高,单位m
     */
    private Integer takeOffLandAlt;

    /**
     * 倾斜角度
     */
    private Integer tiltAngle;

    /**
     * 测绘基准面高度，单位m
     */
    private Integer surveyDatumAlt;

    /**
     * 定时拍照时间间隔
     */
    private Float shootPhotoTimeInterval;

    /**
     * 定距拍照时间间隔
     */
    private Float shootPhotoDistanceInterval;

    /**
     * 计划模式：1，2，3，4，5
     */
    private Integer planMode;

    /**
     * 航线航点别名
     */
    private String byname;

    /**
     * 识别类型
     */
    private List<Integer> photoPropList;

    private TaskMode taskMode;

    public enum TaskMode {
        CAMERA_TIMER, CAMERA_DISTANCE
    }
}
