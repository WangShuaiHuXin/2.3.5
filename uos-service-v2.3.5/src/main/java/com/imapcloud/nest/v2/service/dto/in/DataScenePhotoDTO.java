package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 现场取证
 *
 * @author boluo
 * @date 2022-07-13
 */
@ToString
public final class DataScenePhotoDTO {

    private DataScenePhotoDTO() {

    }

    @Data
    public static class UpdateInfo {

        /**
         * 操作人帐户id
         */
        private String accountId;

        private String addr;

        private String execMissionId;

        private BigDecimal longitude;

        private BigDecimal latitude;

        private Long topicLevelId;

        private Integer industryType;

        private Long topicProblemId;

        private BigDecimal recX;

        private BigDecimal recY;

        private BigDecimal recWidth;

        private BigDecimal recHeight;

        private BigDecimal relX;

        private BigDecimal relY;

        private BigDecimal cutWidth;

        private BigDecimal cutHeight;

        private BigDecimal picScale;

        private String imagePath;
    }
}
