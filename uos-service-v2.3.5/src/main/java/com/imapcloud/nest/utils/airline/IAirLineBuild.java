package com.imapcloud.nest.utils.airline;

import com.imapcloud.sdk.pojo.entity.Waypoint;

import java.util.List;

/**
 * 航点解析工具类
 *
 * @author wmin
 */
public interface IAirLineBuild {

    /**
     * 构建航线
     * @param airLineParam
     * @return
     */
    List<Waypoint> buildAirLine(AirLineParams airLineParam);
}
