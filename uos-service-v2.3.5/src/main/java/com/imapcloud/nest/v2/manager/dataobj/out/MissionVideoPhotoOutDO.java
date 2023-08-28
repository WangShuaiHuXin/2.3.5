package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频照片
 *
 * @author boluo
 * @date 2022-11-01
 */
@Data
public class MissionVideoPhotoOutDO {

    private Long id;

    /**
     * 视屏抽取时间
     */
    private LocalDateTime extractTime;

    /**
     * 架次id
     */
    private Integer missionId;

    /**
     * 图片链接
     */
    private String photoUrl;

    /**
     * 缩略图url
     */
    private String thumbnailUrl;

    private LocalDateTime createTime;
}
