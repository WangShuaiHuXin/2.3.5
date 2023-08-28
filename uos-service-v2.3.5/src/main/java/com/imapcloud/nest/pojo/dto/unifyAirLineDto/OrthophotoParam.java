package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import lombok.Data;

import java.util.List;

/**
 * Created by wmin on 2020/11/16 17:43
 * 正射影像参数
 *
 * @author wmin
 */
@Data
public class OrthophotoParam {
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
     * 是否旋转90
     */
    private Boolean rotate90;

    /**
     * 航线高度,单位m
     */
    private Integer lineAlt;

    /**
     * 起降航高,单位m
     */
    private Integer takeOffLandAlt;

    /**
     * 测绘基准面高度，单位m
     */
    private Integer surveyDatumAlt;

    /**
     * 定时拍照间隔
     */
    private Float shootPhotoTimeInterval;

    /**
     * 定居拍照间隔
     */
    private Float shootPhotoDistanceInterval;

    /**
     * 航线航点别名
     */
    private String byname;

    /**
     * 识别类型
     */
    private List<Integer> photoPropList;

    /**
     * 任务模式
     */
    private TaskMode taskMode;

    public enum TaskMode {
        CAMERA_TIMER, CAMERA_DISTANCE
    }
}
