package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据分析结果
 *
 * @author boluo
 * @date 2022-07-18
 */
@ToString
public class DataAnalysisResultInPO {

    private DataAnalysisResultInPO() {}

    @Data
    public static class ProblemTrendIn {

        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private List<Integer> orgList;
        private String topicKey;
    }

    @SuperBuilder
    public static class ProblemIn extends QueryCriteriaDo<ProblemIn> {

        // 必填
        private LocalDateTime startTime;

        // 必填
        private LocalDateTime endTime;

        // 必填
        private String topicKey;

        // 必填
        private String orgCode;

        // 过滤权限
        private String visibleOrgCode;

        /**
         * 专题行业类型
         */
        private Integer industryType;

        /**
         * 专题级别id
         */
        private String topicLevelId;

        /**
         * 标签名
         */
        private String tagName;

        /**
         * 问题类型
         */
        private Long topicProblemId;
    }
}
