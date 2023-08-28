package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 视频抽帧开启信息
 * @author Vastfy
 * @date 2023/01/31 15:12
 * @since 1.0.0
 */
@Data
public class VideoExtractionStartOutDO implements Serializable {

    /**
     * 抽帧任务ID
     */
    private String extractionId;

    /**
     * 总帧数
     */
    private int totalFrames;

    /**
     * 起始解析帧
     */
    private int startFrame;

    /**
     * 解析跨度
     */
    private int frameSpan;

    /**
     * 预估抽帧数量
     */
    private int estimatedCounts;


}
