package com.imapcloud.nest.v2.dao.po.out;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MissionMediaErrLogOutPO {
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
    private Boolean deleted;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
}
