package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 地图图层数据视图
 * @author Vastfy
 * @date 2022/9/23 17:27
 * @since 2.1.0
 */
@Data
@ApiModel("地图图层信息")
public class MaplayerInfoRespVO implements Serializable {

    @ApiModelProperty(value = "图层ID", position = 1, example = "1")
    private String id;

    @ApiModelProperty(value = "图层名称", position = 2, example = "新建图层")
    private String name;

    @ApiModelProperty(value = "所属单位编码", position = 3, example = "000")
    private String orgCode;

    @ApiModelProperty(value = "所属单位名称", position = 4, example = "中科云图")
    private String orgName;

    @ApiModelProperty(value = "发布地址", position = 5, example = "http://www.baidu.com")
    private String route;

    @ApiModelProperty(value = "图层经度", position = 6, example = "112.666")
    private Double longitude;

    @ApiModelProperty(value = "图层纬度", position = 7, example = "27.666")
    private Double latitude;

    @ApiModelProperty(value = "定位高度", position = 8, example = "11.11")
    private Double altitude;

    @ApiModelProperty(value = "显示高度", position = 9, example = "666.11")
    private Double viewAltitude;

    @ApiModelProperty(value = "【本单位】是否展示", position = 10, example = "false")
    private Boolean display;

    @ApiModelProperty(value = "【本单位】是否预加载", position = 11, example = "false")
    private Boolean preload;

    @ApiModelProperty(value = "影像层级", position = 12, example = "1")
    private Integer hierarchy;

    @ApiModelProperty(value = "碰撞检测【地形专有】", position = 13, example = "1")
    private Integer safeCheck;

    @ApiModelProperty(value = "几何误差【模型专有】", position = 14, example = "10.01")
    private Double geometricError;

    @ApiModelProperty(value = "图层类型", position = 15, example = "0")
    private Integer type;

    @ApiModelProperty(value = "【图层单位】是否展示", position = 16, example = "false")
    private Boolean display0;

    @ApiModelProperty(value = "【图层单位】是否预加载", position = 17, example = "false")
    private Boolean preload0;

    @ApiModelProperty(value = "偏移高度", position = 18, example = "10")
    private Integer offsetHeight;

}
