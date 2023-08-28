package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 表计数据详情信息
 * @author Vastfy
 * @date 2022/12/04 15:59
 * @since 2.1.5
 */
@Data
public class MeterDataDetailInfoOutDTO implements Serializable {

    private String dataId;

    private String detailId;

    private String detailName;

    private String originalPicUrl;

    private LocalDateTime shootingTime;

    private Integer deviceState;

    private Integer readingState;

    private String discernPicUrl;

    private String alarmReason;

    private List<ReadingInfo> readingInfos;

    private String areaLayerName;

    private String subAreaLayerName;

    private String unitLayerName;

    private String deviceLayerName;

    private String componentId;

    private String componentName;

    private String verificationStatus;

    private String orgCode;

    /**
     * 设备台账名称
     */
    private String equipmentName;

    @Data
    public static class ReadingInfo implements  Serializable{

        private String ruleId;

        private String ruleName;

        private String readingValue;

        private Boolean valid;

    }

}
