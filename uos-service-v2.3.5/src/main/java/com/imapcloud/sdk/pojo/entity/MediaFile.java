package com.imapcloud.sdk.pojo.entity;

import lombok.Data;

/**
 * 多媒体
 *
 * @author: zhengxd
 * @create: 2020/9/8
 **/
@Data
public class MediaFile {
    /**
     * 文件id
     * 任务执行id
     * 文件名称
     * 文件类型
     * 文件创建时间
     * 是否已经下载到机巢
     * 图片经纬度、海拔信息
     */
    private String fileId;
    private String execMissionID;
    private String fileName;
    private Long fileSize;
    private String filePath;
    private String uploadPath;
    private Integer mediaType;
    private Long timeCreated;
    private String fileDataStoragePath;
    private Boolean fileDataDownloaded;
    private Boolean thumbnailDownloaded;
    private Boolean saved;
    private Double longitude;
    private Double latitude;
    private Double altitude;

    /**
     * 航点序号
     */
    private Integer waypointIndex;

    /**
     * 手动拍照：true CPS2.1.1新增
     */
    private boolean manualRecord;

    /**
     * 航点序号 CPS2.1.1新增
     */
    private Integer shootIndex;

    /**
     * 镜头类型 0-普通可见光镜头 1-广角可见光镜头 2-变焦可见光镜头 3-热红外镜头
     */
    private int lenType;
}
