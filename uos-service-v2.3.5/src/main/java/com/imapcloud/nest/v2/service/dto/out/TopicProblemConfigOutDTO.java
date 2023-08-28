package com.imapcloud.nest.v2.service.dto.out;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 专题dto
 *
 * @author boluo
 * @date 2022-07-14
 */

@ToString
@Data
public class TopicProblemConfigOutDTO {

    private TopicProblemConfigOutDTO() {

    }

    @Data
    public static class LevelInfoOut {

        /**
         * 专题级别id
         */
        private Long topicLevelId;

        /**
         * 专题级别名称
         */
        private String topicLevelName;

        /**
         * 专题级别code
         */
        private String topicLevelCode;
    }

    @Data
    public static class IndustryProblemListOut {
        /**
         * 专题行业问题id
         */
        @ApiModelProperty(value = "专题行业问题id", example = "10000")
        private String topicProblemId;

        /**
         * 专题行业问题名称
         */
        @ApiModelProperty(value = "专题行业问题名称", example = "违停")
        private String topicProblemName;

        /**
         * 专题行业id
         */
        @ApiModelProperty(value = "专题行业ID", example = "10000")
        private String industryType;

        /**
         * 专题行业id
         */
        @ApiModelProperty(value = "专题行业名称", example = "10000")
        private String topicIndustryName;

        /**
         * 来源
         */
        @ApiModelProperty(value = "来源", example = "-1")
        private Integer source;

        @ApiModelProperty(value = "关联识别功能标签", example = "智慧城管/货车识别/货车;工程车")
        private String associatedFunctionTag;

        @ApiModelProperty(value = "关联识别功能标签")
        private List<ProblemTypeConfigUDAOut> associatedFunctionTagList;

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
        public static class ProblemTypeConfigUDAOut {

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
    }

    @Data
    public static class AddIndustryProblemOut {

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
        private List<UDAProblemTypeOut> associatedFunctionTagList;

        @ApiModelProperty(value = "单位编码")
        private String orgCode;

        @Data
        public static class UDAProblemTypeOut {

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
    }


    @Data
    public static class EditIndustryProblemOut {
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
        private List<UDAProblemTypeOut> associatedFunctionTagList;

        @ApiModelProperty(value = "单位编码")
        private String orgCode;

        /**
         * 专题行业问题ID
         */
        @ApiModelProperty(value = "专题行业问题ID", example = "10000")
        private String topicProblemId;

        @Data
        public static class UDAProblemTypeOut {

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
    }
}
