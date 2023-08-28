package com.imapcloud.nest.v2.dao.po.in;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Classname GridHistoryPhotoPO
 * @Description GridHistoryPhotoPO
 * @Date 2023/1/6 10:55
 * @Author Carnival
 */
@Data
public class GridHistoryPhotoPO {

    private Long id;

    private String name;

    private String photoUrl;

    private Long photoSize;

    private String thumbnailUrl;

    private Integer missionId;

    private Integer missionRecordsId;

    private Double latitude;

    private Double longitude;

    private Double altitude;

    private Integer taskId;

    private Integer photoType;

    private Integer lenType;

    private LocalDateTime createTime;

    private String missionName;

    private String taskName;

    private String thumImagePath = "";

    private String resultImagePath = "";

    private String centerDetailId = "";
}
