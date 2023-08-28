package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * @Classname ThumbnailStorageOutDO
 * @Description 缩略图存储信息
 * @Date 2023/2/17 11:29
 * @Author Carnival
 */
@Data
public class ThumbnailStorageOutDO {

    /**
     * 缩略图ID
     */
    private String thumbnailId;

    /**
     * 缩略图存储路径，示例：/store/2023/01/3A
     */
    private String storagePath;

    /**
     * 缩略图名称，示例：4f06d68c39068d03e4e19712b8d27893_65x78.jpg
     */
    private String filename;

    /**
     * 原图存储路径
     */
    private String srcImagePath;

    private Double scaleWidth;

    private Double scaleHeight;
}
