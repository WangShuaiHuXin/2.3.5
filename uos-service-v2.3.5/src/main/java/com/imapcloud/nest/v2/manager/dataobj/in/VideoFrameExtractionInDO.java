package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 视频抽帧请求信息
 * @author Vastfy
 * @date 2023/01/31 15:12
 * @since 1.0.0
 */
@Data
@ToString(callSuper = true)
public class VideoFrameExtractionInDO implements Serializable {

    /**
     * 视频文件URI地址：/store/aa/bb.mp4
     */
    private String videoUri;

    /**
     * 抽帧开始时间，即取第一张图片的时间，默认视频起止时刻
     */
    private Integer startTime;

    /**
     * 抽帧间隔，单位：ms
     */
    private Integer intervalMills;

    /**
     * 延迟抽帧时间，单位：ms
     */
    private Integer delayMills;

    /**
     * 是否自动生成缩略图，默认值：false
     */
    private Boolean autoThumbnail;

    /**
     * 抽帧进度回调地址
     */
    private String callbackUrl;

}
