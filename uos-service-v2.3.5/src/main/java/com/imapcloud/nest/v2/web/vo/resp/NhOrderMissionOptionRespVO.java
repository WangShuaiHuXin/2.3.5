package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class NhOrderMissionOptionRespVO implements Serializable {

    private Integer missionId;

    private Long missionRecordId;

    private String missionName;

    private Integer flyIndex;

    private LocalDateTime startTime;


    private String name;
    private Integer taskId;
    private String taskName;
    private Integer id;
    private LocalDateTime endTime;
    private Integer status;
    private Integer dataStatus;
    private Integer gainDataMode;
    private Integer gainVideo;
    private Integer gainVideoData;
    private String uuid;
    private Integer photoCount;
    private Integer videoCount;
    private Integer airLineId;
    private Integer tagId;
    private String tagName;
    private Integer uavWhich;
}
