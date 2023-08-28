package com.imapcloud.nest.v2.service.dto.in;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DjiAirLineDTO {

    private String mode;

    private MapConfigs mapConfigs;

    private LineConfigs lineConfigs;

    @Data
    public static class Location{

        private BigDecimal lng;

        private BigDecimal lat;

        private BigDecimal alt;

        private BigDecimal ellipsoidHeight;

    }

    @Data
    public static class Points{

        private Location location;

        private Integer speed;

        private BigDecimal heading;

        private Integer followAction;

        private Integer followAlt;

        private Integer followGimbalPitch;

        private Integer followSpeed;

        private Integer followTag;

        private List<CustomActions> customActions;

    }

    @Data
    public static class CustomActions{

        private String actionType;

        private BigDecimal value;

        private Integer[] identifyType;

        private String byname;
    }

    @Data
    public static class MapConfigs{

        private List<Points> points;

    }

    @Data
    public static class LineConfigs{
        @JSONField(name="DJI_KML")
        private DJIKml djiKml;
    }

    @Data
    public static class DJIKml{

        private Integer speed;

        private Integer takeOffLandAlt;

        private Integer autoFlightSpeed;

        private String headingMode;

        private Integer globalHeight;

        private Boolean absolute;

        private BigDecimal distance;
        //航线时长/s
        private BigDecimal duration;

        private String heightMode;
        //相对起飞点经度
        private BigDecimal refPointLng;
        //相对起飞点纬度
        private BigDecimal refPointLat;
        //相对起飞点高度
        private BigDecimal refPointAlt;
        //相对起飞点对地高度
        private BigDecimal refPointAglAlt;
    }



}
