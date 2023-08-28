package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname GridMissionRecordPageReqVO
 * @Description 网格任务记录分页展示VO
 * @Date 2022/12/21 11:52
 * @Author Carnival
 */
@ApiModel("网格任务记录分页展示VO")
@Data
public class GridMissionRecordPageReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "任务名", example = "网格测试")
    private String missionName;

    @ApiModelProperty(value = "管理网格ID", example = "1522445222214")
    private String gridManageId;

    @ApiModelProperty(value = "基站ID", example = "1522445222214")
    private String baseNestId;

    @ApiModelProperty(value = "基站名称", example = "G900-6")
    private String baseNestName;

    @ApiModelProperty(value = "开始时间", example = "2022-12-21")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2022-12-21")
    private String endTime;

    @ApiModelProperty(value = "单位", example = "000")
    private String orgCode;
}
