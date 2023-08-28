package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisDetailPageOutDTO implements Serializable {

    private Long centerBaseId;

    private Long centerDetailId;

    private Long photoId;

    private String photoName;

    private Integer photoState;

    private Integer pushState;

    private Long tagId;

    private Long taskId;

    private Long missionId;

    private Long missionRecordsId;

    private Long nestId;

    private String orgId;

    private String thumImageMarkPath;

    private String imageMarkPath;

    private String imagePath;

    private String thumImagePath;

    private Integer srcDataType;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Integer originalWidth;

    private Integer originalHeight;

    private LocalDateTime createdTime;

    private LocalDateTime photoCreateTime;

    private String missionFlyIndex;

    private Integer aiAnalysis;


}
