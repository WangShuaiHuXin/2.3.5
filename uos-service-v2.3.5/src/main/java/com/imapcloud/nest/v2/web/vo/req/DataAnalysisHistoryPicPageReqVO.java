package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisHistoryPicPageReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "任务表主键", position = 3, required = false, example = "")
    @NotNull(message = "{geoai_uos_cannot_empty_taskid}")
    private String taskId;
    @ApiModelProperty(value = "分析中心明细表主键", position = 4, required = false, example = "")
    @NotNull(message = "{geoai_uos_cannot_empty_detailid}")
    private String detailId;
    @ApiModelProperty(value = "经度", position = 5, required = false, example = "")
    @NotNull(message = "{geoai_uos_cannot_empty_longitude_cn}")
    private BigDecimal longitude;
    @ApiModelProperty(value = "纬度", position = 6, required = false, example = "")
    @NotNull(message = "{geoai_uos_cannot_empty_latitude_cn}")
    private BigDecimal latitude;
    @ApiModelProperty(value = "距离", position = 7, required = false, example = "")
    @NotNull(message = "{geoai_uos_cannot_empty_distance}")
    private Integer distinct;
    @ApiModelProperty(value = "开始时间", position = 8, required = false, example = "")
    @NotNull(message = "{geoai_uos_cannot_empty_start_time}")
    private String startTime;
    @ApiModelProperty(value = "经度", position = 9, required = false, example = "")
    @NotNull(message = "{geoai_uos_cannot_empty_endtime}")
    private String endTime;

    @ApiModelProperty(value = "架次", position = 10, required = false, example = "")
    @NotNull(message = "{geoai_uos_sortie_id_cannot_empty}")
    private Long missionId;

    @ApiModelProperty(value = "架次记录", position = 11, required = false, example = "")
    private Long missionRecordId;
}