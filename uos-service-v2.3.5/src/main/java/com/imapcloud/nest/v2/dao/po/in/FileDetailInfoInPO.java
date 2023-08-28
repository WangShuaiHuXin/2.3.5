package com.imapcloud.nest.v2.dao.po.in;

import lombok.Data;
import lombok.ToString;

/**
 * 文件详细信息
 *
 * @author boluo
 * @date 2022-10-31
 */
@ToString
public class FileDetailInfoInPO {

    private FileDetailInfoInPO() {}

    @Data
    public static class DayReportInPO {

        /**
         * 单位code
         */
        private String orgCode;

        /**
         * 基站id
         */
        private String nestId;

        /**
         * 标签版本
         */
        private Integer tagVersion;

        private String startTime;

        private String endTime;
    }
}
