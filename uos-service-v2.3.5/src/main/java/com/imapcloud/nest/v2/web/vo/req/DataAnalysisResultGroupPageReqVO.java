package com.imapcloud.nest.v2.web.vo.req;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Classname DataAnalysisResultGroupPageReqVO
 * @Description 数据分析问题统计结果分组页面VO
 * @Date 2022/10/11 15:45
 * @Author Carnival
 */
@ApiModel("问题统计结果分页请求")
@Data
public class DataAnalysisResultGroupPageReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "单位编码", position = 1, example = "")
    private String orgCode;

    @ApiModelProperty(value = "问题等级", position = 2, required = false, example = "")
    private Long topicLevelId;

    @ApiModelProperty(value = "行业类型", position = 3, required = false, example = "")
    private Integer industryType;

    @ApiModelProperty(value = "问题类型", position = 4, required = false, example = "")
    private Long topicProblemId;

    @ApiModelProperty(value = "开始时间", position = 5, required = false, example = "")
    private String startTime;

    @ApiModelProperty(value = "结束时间", position = 6, required = false, example = "")
    private String endTime;

    @ApiModelProperty(value = "标签名", position = 7, required = false, example = "")
    private String tagName;

    @ApiModelProperty(value = "专题Key", position = 8, required = false, example = "")
    private String topicKey;

    @ApiModelProperty(value = "单位编码(旧)", position = 9, example = "")
    private String orgId;
}
