package com.imapcloud.nest.pojo.dto;

import com.imapcloud.sdk.pojo.entity.Mission;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 任务管理页面的任务传输类
 *
 * @author wmin
 */
@Data
public class TaskOfManageTaskDto {
    /**
     * 任务Id
     */
    private Integer id;
    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务类型
     */
    private Integer taskType;
    /**
     * 最后修改时间
     */
    private LocalDate lastModifyDate;
    /**
     * 航点数量
     */
    private Integer pointCount;

    /**
     * 航线总长
     */
    private Double airLineLength;

    /**
     * 预计飞行时间,单位是秒
     */
    private long predictFlyTime;

    /**
     * 拍照数
     */
    private Integer photoCount;

    /**
     * 航点列表，[[{index:1},{lat:xxx},{lng:xxx}],[{index:1},{lat:xxx},{lng:xxx}]]
     */
    private String waypointList;

    /**
     * 航线map
     * key -> 航线id
     * val -> 航线
     */
    private Map<Integer, String> airLineMap;

    /**
     * 是否是多架次航线的
     */
    private boolean multiMission;

    /**
     * missionId
     */
    private String missionId;

    /**
     * 航线类型
     */
    private Integer airLineType;

    /**
     * 基站航线信息
     */
    private Mission missionDetail;

    private Boolean absolute;

}
