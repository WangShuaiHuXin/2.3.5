package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 表计读数识别信息
 * @author Vastfy
 * @date 2022/12/04 16:34
 * @since 2.1.5
 */
@Data
public class MeterReadingDiscernInfoInDTO implements Serializable {

    private String algorithmPicUrl;

    private String discernPicPath;

    private Integer readingState;

    private Integer deviceState;

    private Integer alarmReasonType;

    private List<MeterReadingValue> readingValues;

    @Data
    public static class MeterReadingValue implements Serializable{

        private String readingRuleId;

        /**
         * 部件规则名称
         */
        private String readingRuleName;

        /**
         * 读数值
         */
        private String readingValue;

        /**
         * 读数值是否符合标准
         */
        private Boolean valid;

        /**
         * 备注信息（一般用于在记录规则匹配失败原因）
         */
        private String remark;

    }

}
