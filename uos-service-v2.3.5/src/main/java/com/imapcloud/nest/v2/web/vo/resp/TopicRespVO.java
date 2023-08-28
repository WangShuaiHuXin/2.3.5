package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;
import lombok.ToString;

/**
 * 专题
 *
 * @author boluo
 * @date 2022-07-14
 */
@ToString
public class TopicRespVO {

    private TopicRespVO() {

    }

    @Data
    public static class LevelInfoResp {

        /**
         * 专题级别id
         */
        private String topicLevelId;

        /**
         * 专题级别名称
         */
        private String topicLevelName;

        /**
         * 专题级别code
         */
        private String topicLevelCode;
    }
}
