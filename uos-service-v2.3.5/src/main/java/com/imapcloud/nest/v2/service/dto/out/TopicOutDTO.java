package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.ToString;

/**
 * 专题dto
 *
 * @author boluo
 * @date 2022-07-14
 */

@ToString
public class TopicOutDTO {

    private TopicOutDTO() {

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
    public static class IndustryListOut {

        /**
         * 专题行业类型
         */
        private Integer industryType;

        /**
         * 专题行业名称
         */
        private String topicIndustryName;
    }

    @Data
    public static class IndustryProblemListOut {

        /**
         * 专题行业id
         */
        private Long topicProblemId;

        /**
         * 专题行业名称
         */
        private String topicProblemName;

        /**
         * 来源
         * -1：系统默认
         * 0：单位自定义
         */
        private Integer source;

        /**
         * 行业类型，取字典`GEOAI_INDUSTRY_TYPE`数据项项
         */
        private Integer industryType;

    }
}
