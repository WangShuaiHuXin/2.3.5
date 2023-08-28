package com.imapcloud.nest.pojo.dto.app;

import lombok.Data;

import java.util.List;

/**
 * @author wmin
 */
@Data
public class AppLocalCoordinateDTO {

    private List<Coordinate> route;

    @Data
    public static class Coordinate {
        private Double alt;
        private Double lat;
        private Double lng;
    }

}
