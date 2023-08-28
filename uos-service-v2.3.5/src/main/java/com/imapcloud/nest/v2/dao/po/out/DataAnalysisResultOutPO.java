package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;
import lombok.ToString;

/**
 * 数据分析结果
 *
 * @author boluo
 * @date 2022-07-18
 */
@ToString
public class DataAnalysisResultOutPO {

    private DataAnalysisResultOutPO() {}

    @Data
    public static class CollectSumOut {

        /**
         * 问题等级
         */
        private Long topicLevelId;

        /**
         * 问题数量
         */
        private Long problemNum;
    }

    @Data
    public static class ProblemTrendOut {

        /**
         * 日期
         */
        private String localDate;

        /**
         * 问题数量
         */
        private Long problemNum;
    }

    @Data
    public static class GroupInfoOutPO {

        private String resultGroupId;

        private int num;

        private String thumImagePath;
    }
}
