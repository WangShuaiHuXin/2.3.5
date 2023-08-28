package com.imapcloud.nest.v2.web.vo.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 专题问题类型检索条件
 * @author mr
 * @date 2022/10/31 11:52
 * @since 2.0.0
 */
@ApiModel("专题问题类型检索条件")
@Data
@ToString(callSuper = true)
public class TopicProblemConfigReqVO {

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class TopicProblemConfigReq extends PageInfo {
        @ApiModelProperty(value = "专题行业ID", example = "1")
        String industryType;
        @ApiModelProperty(value = "来源", example = "-1")
        String source;
        @ApiModelProperty(value = "专题行业问题名称", example = "违停")
        String topicProblemName;
    }

    @Data
    public static class AddIndustryProblemReq {

        /**
         * 专题行业问题名称
         */
        @ApiModelProperty(value = "专题行业问题名称", example = "违停")
        private String topicProblemName;

        /**
         * 专题行业id
         */
        @ApiModelProperty(value = "专题行业Type", example = "10000")
        private Long industryType;

        /**
         * 来源
         */
        @ApiModelProperty(value = "来源", example = "-1")
        private Integer source;

        @ApiModelProperty(value = "关联识别功能标签")
        private List<ProblemTypeConfigReq> associatedFunctionTagList;

        /**
         * 单位编码
         */
        private String orgCode;
    }

    @Data
    public static class EditIndustryProblemReq {
        /**
         * 专题行业问题名称
         */
        @ApiModelProperty(value = "专题行业问题名称", example = "违停")
        private String topicProblemName;

        /**
         * 专题行业id
         */
        @ApiModelProperty(value = "专题行业Type", example = "10000")
        private Long industryType;

        /**
         * 来源
         */
        @ApiModelProperty(value = "来源", example = "-1")
        private Integer source;

        /**
         * 专题行业问题ID
         */
        @ApiModelProperty(value = "专题行业问题ID", example = "10000")
        private String topicProblemId;

        @ApiModelProperty(value = "关联识别功能标签")
        private List<ProblemTypeConfigReq> associatedFunctionTagList;

        /**
         * 单位编码
         */
        private String orgCode;
    }

    @Data
    public static class ProblemTypeConfigReq {

        @ApiModelProperty(value = "uda场景ID")
        @TableField("storage_id")
        private String storageId;

        @ApiModelProperty(value = "uda场景名称")
        @TableField("storage_name")
        private String storageName;

        @ApiModelProperty(value = "uda识别功能ID")
        @TableField("function_id")
        private String functionId;

        @ApiModelProperty(value = "uda识别功能名称")
        @TableField("function_name")
        private String functionName;

        @ApiModelProperty(value = "uda问题类型配置ID")
        @TableField("type_relation_id")
        private String typeRelationId;

        @ApiModelProperty(value = "uda问题类型配置名称")
        @TableField("type_relation_name")
        private String typeRelationName;
    }

    @Data
    public static class UDAProblemTypeReq {

        @ApiModelProperty(value = "uda场景ID/识别功能ID/问题类型ID")
        @TableField("id")
        private String id;

        @ApiModelProperty(value = "uda场景名称/uda场景名称/识别功能名称/问题类型名称")
        @TableField("name")
        private String name;

        @ApiModelProperty(value = "问题类型是否可用（0：不可选；1可选）")
        @TableField("status")
        private Integer status;
    }
}
