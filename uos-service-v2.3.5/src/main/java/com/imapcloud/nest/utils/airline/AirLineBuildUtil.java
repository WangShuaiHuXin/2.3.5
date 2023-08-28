package com.imapcloud.nest.utils.airline;

import com.imapcloud.sdk.pojo.entity.Waypoint;

import java.util.Collections;
import java.util.List;

/**
 * 航线构建工作类
 *
 * @author wmin
 */
public class AirLineBuildUtil {

    public static List<Waypoint> buildAirLine(AirLineParams param) {
        if (param == null) {
            return Collections.emptyList();
        }
        IAirLineBuild iAirLineBuild;
        switch (param.getAirLineType()) {
            case 1:
                iAirLineBuild = new UnifyAirLineBuild();
                break;
            case 2:
                iAirLineBuild = new PointCloudAirLineBuild();
                break;
            case 3:
                //机巢航线json数据格式
                iAirLineBuild = new NestAirLineBuild();
                break;
            default:
                return Collections.emptyList();
        }

        return new AirLineContext(iAirLineBuild).buildAirLine(param);
    }
}
