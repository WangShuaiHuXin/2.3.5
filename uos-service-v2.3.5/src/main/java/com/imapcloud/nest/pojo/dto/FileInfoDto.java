package com.imapcloud.nest.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileInfoDto {
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 存储地址
     */
    private String uploadPath;
    /**
     * 文件类型
     */
    private Integer type;
    private Integer missionId;
    private Integer missionRecordsId;
    private Integer srtId;
    private Boolean isExtractSrt;
    private Integer taskId;
    private String taskName;
    private LocalDateTime missionRecordTime;
    private Integer tagId;
    private String filePath;
    private Long fileSize;
    private Integer fileInfoId;
    private String unitId;

    private Integer pollutionType;

}

