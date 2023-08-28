package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;

/**
 * @Classname GridDataStatusPO
 * @Description GridDataStatusPO
 * @Date 2022/12/24 17:06
 * @Author Carnival
 */
@Data
public class GridMissionRecordsPO {

    private Integer taskId;

    private Integer missionId;

    private Integer missionRecordsId;

    private String missionName;

    private String gridManageId;

    private String taskName;

    private Integer dataStatus;

    private String orgCode;

    private String gridInspectId;
}
