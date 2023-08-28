package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 区域基站信息响应视图对象示例
 * @author Vastfy
 * @date 2022/07/08 11:12
 * @since 1.9.7
 */
@ApiModel("基站基本信息")
@Data
public class NestBasicRespVO implements Serializable {

    @ApiModelProperty(value = "基站编号", position = 1, required = true, example = "9527")
    private String id;

    @ApiModelProperty(value = "基站唯一ID", position = 1, required = true, example = "fb16f2eb-f7da-4bd9-b2a6-fd4de1156268")
    private String uuid;

    @ApiModelProperty(value = "基站名称", position = 2, required = true, example = "广州9527基站")
    private String name;

    @ApiModelProperty(value = "基站状态【参见CPS状态】", position = 3, required = true, example = "1")
    private Integer state;

    @ApiModelProperty(value = "基站基础状态", position = 3, required = true, example = "1")
    private String baseState;

    @ApiModelProperty(value = "基站类型【0：G600；1：S100_V1；2：G900；3：T50；4：CAR；5：S100_V2；6：S110；7：I_CREST2；8：未知】", position = 4, required = true, example = "2")
    private Integer type;

    @ApiModelProperty(value = "是否展示监控【0：不展示；1：展示】", position = 4, required = true, example = "0")
    private Integer showStatus;

    private List<String> uavIds;

}
