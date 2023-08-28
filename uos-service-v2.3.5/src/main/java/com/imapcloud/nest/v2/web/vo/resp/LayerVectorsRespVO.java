package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname LayerVectorsRespVO
 * @Description 网格图层VO
 * @Date 2022/12/7 13:55
 * @Author Carnival
 */
@Data
public class LayerVectorsRespVO {

    @ApiModelProperty(value = "网格矢量ID",  example = "6734437677645678")
    private String layerVectorId;

    @ApiModelProperty(value = "网格矢量名称",  example = "南海图层")
    private String layerVectorName;

    @ApiModelProperty(value = "所属单位",  example = "中科云图")
    private String orgCode;

    @ApiModelProperty(value = "网络矢量Json",  example = "Json")
    private String layerVectorJson;
}
