package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频
 *
 * @author boluo
 * @date 2022-11-01
 */
@Data
public class MissionVideoOutDO {

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

    /**
     * 架次id
     */
    private Integer missionId;

    /**
     * 图片在机巢的文件创建时间
     */
    private LocalDateTime timeCreated;

    private LocalDateTime createTime;
}
