package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaAirLineDTO.java
 * @Description DataPanoramaAirLineDTO
 * @createTime 2022年09月30日 15:19:00
 */
@Data
public class DataPanoramaAirLineDTO {

    private String mode;

    private MapConfigs mapConfigs;

    private LineConfigs lineConfigs;

    @Data
    public static class Location{

        private String airPointId;

        private Integer airPointIndex;

        private String panoName;

        private boolean followAlt;

        private boolean followSpeed;

        private Integer wayPointSpeed;

        private BigDecimal lng;

        private BigDecimal lat;

        private BigDecimal alt;

    }

    @Data
    public static class Points{

        private Location location;

    }

    @Data
    public static class MapConfigs{

        private List<Points> points;

    }

    @Data
    public static class Panorama{

        private String returnMode;

        private Integer takeOffLandAlt;

        private Integer speed;

        private Integer autoFlightSpeed;

        private Integer lineAlt;

        private Integer resolution;

        private String byname;

        private List<Integer> photoPropList;
    }

    @Data
    public static class LineConfigs{

        private Panorama PANORAMA;

    }




}
