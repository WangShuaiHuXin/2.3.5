package com.imapcloud.nest.pojo.dto;

import com.imapcloud.sdk.pojo.entity.Waypoint;
import lombok.Data;

import java.util.List;

/**
 * @author wmin
 */
@Data
public class UnifyNestAirLineResult {
    private List<Waypoint> waypointList;
    private List<List<Waypoint>> waypointsList;
    private int photoCount;
    private int videoCount;
    private int pointCount;
    private boolean result;
    private String msg;
    private boolean isSameRoute = false;
    /**
     * 平台优化版本使用，判断是否需要对mergerCount进行-2操作
     */
    private Boolean  isCps240=false;
}
