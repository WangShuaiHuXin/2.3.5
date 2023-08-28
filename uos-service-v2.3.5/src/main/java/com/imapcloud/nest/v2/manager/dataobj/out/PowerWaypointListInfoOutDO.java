package com.imapcloud.nest.v2.manager.dataobj.out;

import com.imapcloud.nest.v2.service.dto.out.PowerWaypointListInfoOutDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class PowerWaypointListInfoOutDO {
    private Long total;

    private List<PowerWaypointListInfoOutDO.PowerWaypointInfoDO> infoDTOList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PowerWaypointInfoDO {
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
        private Double latitude;
        private Double longitude;
        private Double altitude;
    }
}
