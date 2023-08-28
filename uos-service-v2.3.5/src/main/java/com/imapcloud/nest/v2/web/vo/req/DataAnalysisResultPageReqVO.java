package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisResultPageReqVO extends PageInfo implements Serializable {
//    @ApiModelProperty(value = "页码", position = 1, required = true, example = "")
//    private Integer pageNo;
//    @ApiModelProperty(value = "每页展示数", position = 2, required = true, example = "")
//    private Integer pageSize;
    @ApiModelProperty(value = "结果ID", position = 3, required = false, example = "")
    private Long resultId;
    @ApiModelProperty(value = "图片ID", position = 4, required = false, example = "")
    private Long photoId;
    @ApiModelProperty(value = "问题等级", position = 5, required = false, example = "")
    private Long topicLevelId;
    @ApiModelProperty(value = "行业", position = 6, required = false, example = "")
    private Integer industryType;
    @ApiModelProperty(value = "问题类型", position = 7, required = false, example = "")
    private Long topicProblemId;
    @ApiModelProperty(value = "问题等级描述", position = 8, required = false, example = "")
    private Long topicLevelName;
    @ApiModelProperty(value = "行业描述", position = 9, required = false, example = "")
    private Long topicIndustryName;
    @ApiModelProperty(value = "问题类型描述", position = 10, required = false, example = "")
    private Long topicProblemName;
    @ApiModelProperty(value = "单位ID", position = 11, required = false, example = "")
    private Long orgId;
    @ApiModelProperty(value = "标签名", position = 12, required = false, example = "")
    private String tagName;
    @ApiModelProperty(value = "任务名", position = 13, required = false, example = "")
    private String taskName;
    @ApiModelProperty(value = "架次名", position = 14, required = false, example = "")
    private String missionName;
    @ApiModelProperty(value = "基站名", position = 15, required = false, example = "")
    private String nestName;
    @ApiModelProperty(value = "单位名", position = 16, required = false, example = "")
    private String orgName;
    @ApiModelProperty(value = "开始时间", position = 17, required = false, example = "")
    private String startTime;
    @ApiModelProperty(value = "结束时间", position = 18, required = false, example = "")
    private String endTime;




}
