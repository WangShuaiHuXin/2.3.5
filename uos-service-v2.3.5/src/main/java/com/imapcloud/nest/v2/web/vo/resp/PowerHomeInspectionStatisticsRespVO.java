package com.imapcloud.nest.v2.web.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("首页-巡检统计数据返回")
public class PowerHomeInspectionStatisticsRespVO implements Serializable {
    private String key;
    private String value;
    private String per;

    @Data
    public static class PowerHomeAlarmStatisticsRespVO {
        /**
         * 告警统计、已处理
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private int alarmProcessed;
        /**
         * 告警统计，待处理
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private int alarmPending;
    }
}
