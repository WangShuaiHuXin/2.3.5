package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.Map;

/**
 * Created by wmin on 2020/10/26 16:54
 *
 * @author wmin
 */

@Data
public class FixAirLineDto {
    private Integer id;
    private Integer nestId;
    private String name;
    private String waypoints;
    private Boolean multiMission;
}
