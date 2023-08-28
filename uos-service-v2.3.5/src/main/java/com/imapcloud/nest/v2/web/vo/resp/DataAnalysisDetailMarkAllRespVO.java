package com.imapcloud.nest.v2.web.vo.resp;

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
public class DataAnalysisDetailMarkAllRespVO implements Serializable {

    private String centerBaseId;

    private String centerDetailId;

    private String photoId;

    private String photoName;

    private Integer photoState;

    private Integer pushState;

    private Integer picStatus;

    private String tagId;

    private String taskId;

    private String missionId;

    private String missionRecordsId;

    private String nestId;

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

}
