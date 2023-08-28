package com.imapcloud.nest.utils.airline;

import com.imapcloud.sdk.pojo.entity.Waypoint;

import java.util.List;

/**
 * 航线解析
 *
 * @author wmin
 */
public class AirLineContext {
    private IAirLineBuild iAirLineBuild;

    public AirLineContext(IAirLineBuild iAirLineBuild) {
        this.iAirLineBuild = iAirLineBuild;
    }

    public List<Waypoint> buildAirLine(AirLineParams param) {
        return iAirLineBuild.buildAirLine(param);
    }
}
