package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NhOrderRecordInfoOutPO {

    private Integer missionId;
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
