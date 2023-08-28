package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 专题
 *
 * @author boluo
 * @date 2022-07-14
 */
@ToString
public class TopicReqVO {

    private TopicReqVO() {}

    @Data
    public static class IndustryListReq {
        /**
         * 单位id orgId和execMissionId不能同时为空
         */
        private String orgId;

        /**
         * 架次执行id
         */
        private String execMissionId;

        /**
         * 专题key
         */
        @NotBlank(message = "{geoai_uos_cannot_empty_topic_key}")
        private String topicKey;
    }

    @Data
    public static class EditIndustryReq {

        /**
         * 单位id
         */
        @NotBlank(message = "{geoai_uos_cannot_empty_unitid}")
        private String orgId;

        /**
         * 专题key
         */
        @NotBlank(message = "{geoai_uos_cannot_empty_topic_key}")
        private String topicKey;

        @Valid
        private List<String> industryInfoReqList;
    }

    @Data
    public static class EditIndustryProblemReq {

        /**
         * 专题行业类型
         */
        @NotNull(message = "{geoai_uos_cannot_empty_industry_ID}")
        private Integer industryType;

        /**
         * 单位id
         */
        @NotBlank(message = "{geoai_uos_cannot_empty_unitid}")
        private String orgId;

        /**
         * 专题key
         */
        @NotBlank(message = "{geoai_uos_cannot_empty_topic_key}")
        private String topicKey;

        @Valid
        private List<IndustryProblemInfoReq> industryProblemInfoReqList;
    }

    @Data
    public static class IndustryProblemInfoReq {

        /**
         * 专题行业问题id
         */
        private String topicProblemId;

        /**
         * 专题行业问题名称
         */
        @NotBlank(message = "{geoai_uos_cannot_empty_thematic_industry_issue_name}")
        @Size(max = 20, message = "{geoai_uos_topical_industry_issue_name_up_to_10_words}")
        private String topicProblemName;
    }

    @Data
    public static class IndustryProblemListReq {

        /**
         * 行业类型
         */
        private Integer industryType;

        /**
         * 单位id
         */
        private String orgId;

        /**
         * 专题key
         */
        private String topicKey;
    }
}
