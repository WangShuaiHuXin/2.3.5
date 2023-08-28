package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname GridMissionRecordPageInDTO
 * @Description 网格任务记录分页展示VO
 * @Date 2022/12/21 11:52
 * @Author Carnival
 */
@ApiModel("网格任务记录分页展示VO")
@Data
public class GridMissionRecordPageInDTO extends PageInfo implements Serializable {

    private String missionName;

    private String gridManageId;

    private String baseNestId;

    private String baseNestName;

    private String startTime;

    private String endTime;

    private String orgCode;
}
