package com.imapcloud.nest.v2.service.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SubstationRouteList。json的解析实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PowerEquipmentSubstationRouteListInDTO {
    private String SUBSTATIONGUID;
    private double OFFX;
    private double OFFY;
    private int ZONE;
    private String HEMISPHERE;
    private List<RouteList> ROUTELIST;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RouteList {
        //设备ID
        private String pid;
        private String pos;
        //航点位置信息（经纬度，海拔） "23.04953192, 112.93995363, 47.876"
        private String geopos;
        //航点ID
        private String guid_id;
        private String byname;
       /* private int style;
        private List<String> photoPropList;*/
    }
}
