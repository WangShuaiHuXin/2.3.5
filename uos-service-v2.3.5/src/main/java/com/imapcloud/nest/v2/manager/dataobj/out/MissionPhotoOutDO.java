package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 架次照片
 *
 * @author boluo
 * @date 2022-10-26
 */
@Data
public class MissionPhotoOutDO {

    private Long id;

    /**
     * 图片名字
     */
    private String photoName;

    /**
     * 图片链接
     */
    private String photoUrl;

    /**
     * 缩略图url
     */
    private String thumbnailUrl;

    /**
     * 标签版本
     */
    private Integer tagVersion;

    /**
     * 图片在机巢的文件创建时间
     */
    private LocalDateTime timeCreated;

    /**
     * 架次id
     */
    private Integer missionId;

    /**
     * 记录表id
     */
    private Integer missionRecordsId;

    private LocalDateTime createTime;

    /**
     * 拍照的航点序号
     */
    private Integer waypointIndex;

    /**
     * 镜头类型
     */
    private Integer lenType;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    @Data
    public static class PhotoNumOutDO {

        /**
         * 架次id
         */
        private Integer missionRecordId;

        private long num;
    }
}
