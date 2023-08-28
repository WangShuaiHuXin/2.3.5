package com.imapcloud.nest.v2.web.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("航点台账列表")
public class PowerWaypointListRespVO implements Serializable {

    @ApiModelProperty("部件名称/不存在部件ID的时候为航点名称")
    private String componentName;
    @ApiModelProperty("部件ID")
    private String componentId;
    @ApiModelProperty("本地航点设备台账主键ID")
    private String wayPointStationId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdTime;
    @ApiModelProperty("设备层名称")
    private String deviceLayerName;
    @ApiModelProperty("区域名称")
    private String equipmentAreaName;
    @ApiModelProperty("设备名称")
    private String equipmentName;

    private String equipmentPmsId;
    @ApiModelProperty("关联的设备ID")
    private String equipmentId;
    @ApiModelProperty("设备类型")
    private String equipmentType;

    private String operatorName;
    @ApiModelProperty("间隔单元")
    private String spacingUnit;
    @ApiModelProperty("子区域名称")
    private String subRegionName;
    @ApiModelProperty("变电站名称")
    private String stationName;
    @ApiModelProperty("单元层名称")
    private String unitLayerName;
    @ApiModelProperty("航点ID")
    private String waypointId;
}
