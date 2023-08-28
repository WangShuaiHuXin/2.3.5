package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaRecordsReqVO.java
 * @Description DataPanoramaRecordsReqVO
 * @createTime 2022年09月16日 11:23:00
 */
@Data
public class DataPanoramaRecordsInDTO {

    @Data
    public static class RecordsInDTO extends PageInfo implements Serializable {
        /**
         * 单位编码
         */
        private String orgCode;

        /**
         * 基站id
         */
        private String baseNestId;

        /**
         * 开始时间
         */
        private LocalDate startTime;

        /**
         * 结束时间
         */
        private LocalDate endTime;
    }

    @Data
    public static class PicInDTO implements Serializable {
        /**
         * 架次记录ID
         */
        private String missionRecordsId;

        /**
         * 航点id
         */
        private String airPointId;

        /**
         * 航点序号
         */
        private Integer airPointIndex;

    }

}
