package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 数据分析结果
 *
 * @author boluo
 * @date 2022-07-15
 */
@ToString
public class DataAnalysisResultInDTO {

    private DataAnalysisResultInDTO() {
    }

    @Data
    public static class InsertInfoIn {

        private Long photoId;

        private Long markId;

        private Boolean aiMark;

        private String thumImagePath;

        private String addrImagePath;

        private String resultImagePath;

        private String imagePath;

        private String addr;

        private BigDecimal longitude;

        private BigDecimal latitude;

        private Long topicLevelId;

        private Integer industryType;

        private Long topicProblemId;

        private Long missionRecordsId;

        /**
         * 照片创建时间
         */
        private LocalDateTime photoCreateTime;

        /**
         * 数据来源（0-现场取证、1-照片、2-视频）
         */
        private Integer srcDataType;

        /**
         * 网格id
         */
        private String gridManageId;
    }

    @Data
    public static class ProblemIn extends PageInfo {

        private LocalDateTime startTime;
        private LocalDateTime endTime;

        private String topicKey;

        private String orgCode;

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
