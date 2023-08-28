package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * 存储配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 */
@Data
public class StoreConfig {

    /**
     * 缩略图路径
     */
    private String thumbnailPath;

    /**
     * 下载图片zip包路径
     */
    private String downloadPath;

    /**
     * 录屏的视频路径
     */
    private String videoPath;

    /**
     * 原图、原视频、原全景、原正射影像路径
     */
    private String originPath;

    /**
     * minio的实际路径,主要用作命令行分析本地文件，文件通过手动上传到桶
     */
    private String originRelPath;

    /**
     * ffmpeg地址
     */
    private String videoFfmpegPath;

}
