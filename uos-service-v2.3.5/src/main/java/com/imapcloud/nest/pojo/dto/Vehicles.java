package com.imapcloud.nest.pojo.dto;

import lombok.Data;

/**
 * 任务列表中的架次
 *
 * @author wmin
 */
@Data
public class Vehicles {
    private Integer id;
    private String code;
    private Integer waypointsNum;
    private Double flightDistance;
    private Integer photoCount;
    private Integer videoCount;
    private Long videoLength;
    /**
     * 起降航高
     */
    private Integer takeOffLandAlt;
}
