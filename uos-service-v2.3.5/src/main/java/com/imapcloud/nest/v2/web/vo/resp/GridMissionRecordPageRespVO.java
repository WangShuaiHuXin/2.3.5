package com.imapcloud.nest.v2.web.vo.resp;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname GridMissionRecordPageRespVO
 * @Description 网格任务记录分页展示RespVO
 * @Date 2022/12/21 11:52
 * @Author Carnival
 */
@Data
public class GridMissionRecordPageRespVO extends PageInfo implements Serializable {

    private String gridMissionId;

    private String gridManageId;

    private Integer missionId;

    private String missionName;

    private String baseNestId;

    private String baseNestName;

    private Integer baseType;

    private Integer taskId;

    private Integer syncStatus;

    private String createdTime;

    private Integer missionRecordsId;

    private Integer line;

    private Integer col;

    private Double west;

    private Double east;

    private Double south;

    private Double north;

    private String orgCode;

    private String orgName;
}
