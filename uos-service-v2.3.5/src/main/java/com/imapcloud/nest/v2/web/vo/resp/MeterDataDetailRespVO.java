package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 表计数据详情信息
 * @author Vastfy
 * @date 2022/11/29 10:59
 * @since 2.1.5
 */
@ApiModel("表计数据详情信息")
@Data
public class MeterDataDetailRespVO implements Serializable {

    @ApiModelProperty(value = "表计数据ID", position = 1, example = "95279666")
    private String dataId;

    @ApiModelProperty(value = "表计数据详情ID", position = 2, example = "67890987654")
    private String detailId;

    @ApiModelProperty(value = "表计数据详情名称", position = 3, example = "主变有载调压油位表")
    private String detailName;

    @ApiModelProperty(value = "表计数据原图URL", position = 4, example = "/origin/20221129/originalPicUrl.jpg")
    private String originalPicUrl;

    @ApiModelProperty(value = "表计数据原图拍摄时间", position = 5, example = "2022-11-29 12:34:56")
    private LocalDateTime shootingTime;

    @ApiModelProperty(value = "设备状态【取字典`GEOAI_DIAL_DEVICE_STATE`数据项值】", position = 6, example = "1")
    private Integer deviceState;

    @ApiModelProperty(value = "读数状态【取字典`GEOAI_DIAL_READING_STATE`数据项值】", position = 7, example = "2")
    private Integer readingState;

    @ApiModelProperty(value = "表计数据截图URL", position = 8, example = "/origin/20221129/discernPicUrl.jpg")
    private String discernPicUrl;

    @ApiModelProperty(value = "告警原因", position = 9, example = "存在读数异常")
    private String alarmReason;

    @ApiModelProperty(value = "表计数据原图拍摄时间", position = 10, example = "2022-11-29 12:34:56")
    private List<ReadingInfo> readingInfos;

    @ApiModelProperty(value = "区域层名称", position = 11, example = "500kV设备区")
    private String areaLayerName;

    @ApiModelProperty(value = "子区域层名称", position = 12, example = "低空单元")
    private String subAreaLayerName;

    @ApiModelProperty(value = "单元层名称", position = 13, example = "500kV五哈2号线")
    private String unitLayerName;

    @ApiModelProperty(value = "设备层名称", position = 14, example = "1#A相主变压器")
    private String deviceLayerName;

    @ApiModelProperty(value = "部件ID", position = 15, example = "666")
    private String componentId;

    @ApiModelProperty(value = "核实状态",position = 16,example = "0")
    private String verificationStatus;

    /**
     * 设备台账名称
     */
    private String equipmentName;

    @ApiModel("读数信息")
    @Data
    public static class ReadingInfo implements Serializable{


        @ApiModelProperty(value = "读数规则ID", position = 1, example = "2667733112")
        private String ruleId;

        @ApiModelProperty(value = "读数规则名称", position = 2, example = "泄露电流")
        private String ruleName;

        @ApiModelProperty(value = "读数值", position = 3, example = "10.253")
        private String readingValue;

        @ApiModelProperty(value = "是否符合部件规则", position = 4, example = "true")
        private Boolean valid;

    }

}
