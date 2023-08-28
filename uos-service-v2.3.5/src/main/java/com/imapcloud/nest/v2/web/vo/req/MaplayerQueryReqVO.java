package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 图层查询条件视图
 * @author Vastfy
 * @date 2022/9/23 17:27
 * @since 2.1.0
 */
@Data
@ApiModel("图层查询条件")
public class MaplayerQueryReqVO implements Serializable {

    @ApiModelProperty(value = "图层名称【支持模糊检索】", position = 1, example = "新建图层")
    private String name;

    @ApiModelProperty(value = "单位编码", position = 2, example = "000")
    private String orgCode;

    @ApiModelProperty(value = "图层类型【0：影像；1：地形；2：模型；3：航线；4：矢量；5：其他】", position = 3, example = "1")
    private Integer type;

}
