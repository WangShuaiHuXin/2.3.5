package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 专题
 *
 * @author boluo
 * @date 2022-07-14
 */
@ToString
public class TopicInDTO {

    private TopicInDTO() {}

    @Data
    public static class EditIndustryIn {

        private String orgId;

        private String topicKey;

        private String accountId;

        private List<Integer> industryTypes;
    }

    @Data
    public static class EditIndustryProblemIn {

        private String orgId;

        private Integer industryType;

        private String topicKey;

        private String accountId;

        private List<IndustryProblemIn> industryProblemInList;
    }

    @Data
    public static class IndustryProblemIn {

        private Long topicProblemId;

        private String topicProblemName;
    }
}
