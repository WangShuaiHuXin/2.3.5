package com.imapcloud.nest.utils.airline;

import com.imapcloud.sdk.pojo.entity.Waypoint;

import java.util.List;

/**
 * 平台统一航线构建方法类
 * @author wmin
 */
public class UnifyAirLineBuild implements IAirLineBuild {

    /**
     * 目前只支持dji_kml
     * @param airLineParam
     * @return
     */
    @Override
    public List<Waypoint> buildAirLine(AirLineParams airLineParam) {

        return null;
    }
}
