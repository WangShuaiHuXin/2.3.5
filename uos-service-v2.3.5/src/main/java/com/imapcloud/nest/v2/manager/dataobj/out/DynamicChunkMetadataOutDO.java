package com.imapcloud.nest.v2.manager.dataobj.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Classname DynamicChunkMetadataOutDO
 * @Description 约定分片初始化结果信息
 * @Date 2023/2/16 15:52
 * @Author Carnival
 */
@Data
public class DynamicChunkMetadataOutDO {

    /**
     * 目标文件存储路径
     */
    private String destFilepath;

    /**
     * 目标文件存储名称
     */
    private String destFilename;

    /**
     * 目标文件已上传分片索引列表
     */
    private List<Integer> uploadedChunks;

    /**
     * 目标文件切片上传凭证
     */
    private String uploadToken;

    /**
     * 业务通知ID，回调通知中会包含该值，唯一代表本次上传任务信息
     */
    private String notifyId;
}
