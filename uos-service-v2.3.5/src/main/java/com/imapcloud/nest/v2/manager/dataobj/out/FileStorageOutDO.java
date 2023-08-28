package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * @Classname FileStorageOutDO
 * @Description 文件存储信息
 * @Date 2023/2/16 16:23
 * @Author Carnival
 */
@Data
public class FileStorageOutDO {

    /**
     * 文件存储路径
     */
    private String storagePath;

    /**
     * 文件存储名称
     */
    private String filename;

    /**
     * 文件解压目录，仅设置解压时才有该值
     */
    private String decompressedPath;

}
