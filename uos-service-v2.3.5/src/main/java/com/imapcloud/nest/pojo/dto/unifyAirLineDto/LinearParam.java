package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import com.imapcloud.nest.enums.FocalModeEnum;
import com.imapcloud.sdk.pojo.constant.FlightPathModeEnum;
import lombok.Data;

import java.util.List;

/**
 * Created by wmin on 2020/11/16 17:58
 * 线状巡检参数
 *
 * @author wmin
 */
@Data
public class LinearParam {
    /**
     * 任务模式
     */
    private TaskMode taskMode;

    /**
     * 返航模式
     */
    private ReturnMode returnMode;

    /**
     * 分辨率
     */
    private Double resolution;

    /**
     * 是否起飞录制
     */
    private Boolean takeOffRecord;

    /**
     * 航线高度，单位m
     */
    private Integer lineAlt;

    /**
     * 起降航高，单位m
     */
    private Integer takeOffLandAlt;

    /**
     * 云台角度
     */
    private Integer gimbalPitch;

    /**
     * 飞行速度
     */
    private Integer speed;

    /**
     * 航向重叠度
     */
    private Double routeOverlap;

    /**
     * 飞机旋转角度
     */
    private Integer heading;

    /**
     * 航线航点别名
     */
    private String byname;

    /**
     * 识别类型
     */
    private List<Integer> photoPropList;

    /**
     * 拍照数
     */
    private Integer photoCount;

    /**
     * 视频长度
     */
    private Long videoLength;
    /**
     * 视频个数
     */
    private Integer videoCount;

    /**
     * 拍照时间间隔
     */
    private Float shootPhotoTimeInterval;

    /**
     * 定距拍照
     */
    private Float shootPhotoDistanceInterval;

    /**
     * 变焦模式
     */
    private FocalModeEnum focalMode;

    private FlightPathModeEnum flightPathMode;

    public enum TaskMode {
        VIDEO_CAPTURE, CAMERA_TIMER, CAMERA_DISTANCE
    }

    public enum ReturnMode {
        LINE, ORIGINAL
    }


}
