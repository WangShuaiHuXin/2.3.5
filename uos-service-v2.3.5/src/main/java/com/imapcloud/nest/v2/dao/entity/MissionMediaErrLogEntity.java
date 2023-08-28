package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mission_media_err_log")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MissionMediaErrLogEntity implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Integer missionId;
    /**
     * 架次ID
     */
    private Integer missionRecordId;
    /**
     * 基站UUID
     */
    private String nestUuid;

    /**
     * 参考 MissionMediaErrStatus
     */
    private String errorStep;
    private String errorCode;
    private String errorFile;
    private String errorInfo;
    private String errorSolution;
    private Boolean  deleted;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
}
