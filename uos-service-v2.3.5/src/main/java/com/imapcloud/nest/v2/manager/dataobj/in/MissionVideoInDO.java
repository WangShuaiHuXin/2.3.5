package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

/**
 * 视频
 *
 * @author boluo
 * @date 2022-11-07
 */
@Data
public class MissionVideoInDO {

    private Long id;

    /**
     * 视频链接
     */
    private String videoUrl;

    private String srtUrl;

    /**
     * 视频缩略图
     */
    private String videoThumbnail;
}
