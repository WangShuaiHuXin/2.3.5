package com.imapcloud.nest.v2.web.vo.req;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Classname DataAnalysisTraceSpacetimeReqVO
 * @Description 数据分析问题统计时空追溯请求类
 * @Date 2022/10/14 15:33
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisTraceSpacetimeReqVO {

    @ApiModelProperty(value = "问题组ID", position = 1, required = true, example = "15200001486")
    private String resultGroupId;

    @ApiModelProperty(value = "经度", position = 2, required = true, example = "112.23")
    @NotNull(message = "{geoai_uos_cannot_empty_longitude_cn}")
    private BigDecimal longitude;

    @ApiModelProperty(value = "纬度", position = 3, required = true, example = "23.11")
    @NotNull(message = "{geoai_uos_cannot_empty_latitude_cn}")
    private BigDecimal latitude;

    @ApiModelProperty(value = "开始时间", position = 4, required = true, example = "2022-4-14")
    @NotNull(message = "{geoai_uos_cannot_empty_start_time}")
    private String startTime;

    @ApiModelProperty(value = "结束时间", position = 5, required = true, example = "2022-10-14")
    @NotNull(message = "{geoai_uos_cannot_empty_endtime}")
    private String endTime;

    @ApiModelProperty(value = "匹配半径范围", position = 6, required = true, example = "10.0")
    @NotNull(message = "{geoai_uos_cannot_empty_matching_radius}")
    private Double distance;

    @ApiModelProperty(value = "问题类型", position = 7, required = true, example = "14522220555")
    private Long topicProblemId;

    @ApiModelProperty(value = "架次", position = 8, required = true, example = "14522220555")
    private Long missionId;
}
