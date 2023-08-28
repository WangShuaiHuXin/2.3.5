package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author hc
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("upload")
public class UploadEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 分片数量
     */
    private Integer chunkCount;

    /**
     * 上传文件的md5
     */
    private String fileMd5;

    /**
     * 上传文件/合并文件的格式
     */
    private String suffix;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件地址
     */
    private String filePath;

    /**
     * 上传状态 0.上传完成 1.已上传部分
     */
    private Integer uploadStatus;

    /**
     * 单位Id
     */
    @TableField(exist=false)
    private String unitId;
    /**
     * missionId
     */
    @TableField(exist=false)
    private Integer missionId;
    /**
     * missionRecordsId
     */
    @TableField(exist=false)
    private Integer missionRecordsId;
    @TableField(exist=false)
    private String code;
//    @TableField(exist = false)
    /**
     * exec_mission_id
     */
    @TableField("exec_mission_id")
    private String execMissionID;

    //历史代码未优化成dto
    /**
     * 文件大小
     */
    @TableField(exist = false)
    private Long totalSize;
}
