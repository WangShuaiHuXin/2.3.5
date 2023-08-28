package com.imapcloud.nest.v2.service.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import scala.Int;

import java.io.Serializable;
import java.util.List;

@Data
public class PowerWaypointListInfoOutDTO {
    private Long total;

    private List<PowerWaypointInfoDTO> infoDTOList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PowerWaypointInfoDTO {
        private String componentName;
        private String componentId;
        private String wayPointStationId;
        private String createdTime;
        private String deviceLayerName;
        private String equipmentAreaName;
        private String equipmentName;
        private String equipmentPmsId;
        private String equipmentId;
        private String equipmentType;
        private String operatorName;
        private String spacingUnit;
        private String subRegionName;
        private String stationName;
        private String unitLayerName;
        private String waypointId;
    }
}

