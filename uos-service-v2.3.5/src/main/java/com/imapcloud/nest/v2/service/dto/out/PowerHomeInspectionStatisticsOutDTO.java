package com.imapcloud.nest.v2.service.dto.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PowerHomeInspectionStatisticsOutDTO {

    private String key;
    private String value;
    private String per;

    @Data
    public static class PowerHomeAlarmStatisticsOutDTO {
        /**
         * 告警统计、已处理
         */
        private int alarmProcessed;
        /**
         * 告警统计，待处理
         */
        private int alarmPending;
    }


}
