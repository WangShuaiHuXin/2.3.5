package com.imapcloud.nest.v2.web.vo.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.imapcloud.nest.v2.web.vo.req.TopicProblemConfigReqVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 专题问题类型
 *
 * @author mr
 * @date 2022/10/31 15:12
 * @since 1.0.0
 */
@ApiModel("专题问题类型信息")
@Data
public class TopicProblemRespVO implements Serializable {
    /**
     * 专题行业问题id
     */
    @ApiModelProperty(value = "专题行业问题id", example = "10000")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long topicProblemId;

    /**
     * 专题行业问题名称
     */
    @ApiModelProperty(value = "专题行业问题名称", example = "违停")
    private String topicProblemName;


    @ApiModelProperty(value = "专题行业类型", example = "1")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer industryType;

    /**
     * 专题行业名称
     */
    @ApiModelProperty(value = "专题行业名称", example = "10000")
    private String topicIndustryName;

    /**
     * 来源
     */
    @ApiModelProperty(value = "来源", example = "-1为系统默认、0为单位自定义")
    private String source;

    @ApiModelProperty(value = "关联识别功能标签", example = "智慧城管/货车识别/货车;工程车")
    private String associatedFunctionTag;

    @ApiModelProperty(value = "关联识别功能标签")
    private List<ProblemTypeConfigResp> associatedFunctionTagList;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 单位编码
     */
    @ApiModelProperty(value = "所属单位", example = "中科云图")
    private String orgName;

    @ApiModelProperty(value = "操作人")
    private String modifierId;

    @ApiModelProperty(value = "操作时间")
    private LocalDateTime modifiedTime;

    @Data
    public static class ProblemTypeConfigResp {
        @ApiModelProperty(value = "uda场景ID")
        private String storageId;

        @ApiModelProperty(value = "uda场景名称")
        private String storageName;

        @ApiModelProperty(value = "uda识别功能ID")
        private String functionId;

        @ApiModelProperty(value = "uda识别功能名称")
        private String functionName;

        @ApiModelProperty(value = "uda问题类型配置ID")
        private String typeRelationId;

        @ApiModelProperty(value = "uda问题类型配置名称")
        private String typeRelationName;
    }
}
