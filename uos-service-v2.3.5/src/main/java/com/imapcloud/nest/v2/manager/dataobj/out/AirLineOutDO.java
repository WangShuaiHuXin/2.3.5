package com.imapcloud.nest.v2.manager.dataobj.out;

import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.enums.TaskModeEnum;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 航线
 *
 * @author boluo
 * @date 2022-11-30
 */
@Data
public class AirLineOutDO {

    private Integer airLineId;

    /**
     * 航线名称
     */
    private String name;

    /**
     * 航线的类型，1->航点航线，2->易飞,点云,三维规划航线
     */
    private Integer type;

    /**
     * json格式的航点集合, [{},{},...]，修改过后的航线数据
     */
    private String waypoints;

    /**
     * json格式的航点集合, [{},{},...]，基站航线格式数据数据
     */
    private String originalWaypoints;

    public List<WaypointsInfo> toWaypointsInfoList(Integer taskType) {
        // taskType 任务类型
        if (TaskModeEnum.LOCAL.getValue().equals(taskType)) {
            return JSONUtil.toList(this.waypoints, WaypointsInfo.class);
        }
        return Collections.emptyList();
    }

    @Data
    public static class WaypointsInfo {

        private Integer waypointType;

        private String checkpointuuid;
    }
}
