package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 数据分析结果
 *
 * @author boluo
 * @date 2022-07-18
 */
@ToString
public class DataAnalysisResultReqVO {
    private DataAnalysisResultReqVO() {}

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class BaseReq extends PageInfo {
        // yyyy-MM-dd
        @NotBlank(message = "{geoai_uos_cannot_empty_starttime}")
        private String startTime;

        @NotBlank(message = "{geoai_uos_cannot_empty_endtime}")
        private String endTime;

        @NotBlank(message = "{geoai_uos_cannot_empty_topickey}")
        private String topicKey;

        private LocalDateTime start;

        private LocalDateTime end;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ProblemReq extends BaseReq {

        /**
         * 是否是地图列表
         */
        private boolean map;

        private String orgId;

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
