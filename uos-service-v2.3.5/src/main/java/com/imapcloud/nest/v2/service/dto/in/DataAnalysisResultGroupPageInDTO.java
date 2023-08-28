package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Classname DataAnalysisResultGroupPageInDTO
 * @Description 数据分析问题统计结果分组页面InDTO
 * @Date 2022/10/11 16:13
 * @Author Carnival
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataAnalysisResultGroupPageInDTO extends PageInfo {

    private String orgCode;

    private Long topicLevelId;

    private Integer industryType;

    private Long topicProblemId;

    private String startTime;

    private String endTime;

    private String tagName;

    private String topicKey;
}
