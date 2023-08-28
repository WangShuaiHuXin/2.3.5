package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Classname DataAnalysisTraceSpacetimeOutDTO
 * @Description 数据分析问题统计时空追溯OutDTO
 * @Date 2022/10/14 16:02
 * @Author Carnival
 */
@Data
public class DataAnalysisTraceSpacetimeOutDTO {

    private Long centerBaseId;

    private Long centerDetailId;

    private Long photoId;

    private String photoName;

    private Integer photoState;

    private Integer pushState;

    private Long tagId;

    private Long taskId;

    private Long missionId;

    private Long missionRecordId;

    private String baseNestId;

    private String orgCode;

    private String thumImageMarkPath;

    private String imageMarkPath;

    private String thumImagePath;

    private String imagePath;

    private Integer srcDataType;

    private Integer picType;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Integer originalWidth;

    private Integer originalHeight;

    private LocalDateTime photoCreateTime;

    private Integer isProblem;

    private Integer isResultGroup;

    private String yearMonth;
}
