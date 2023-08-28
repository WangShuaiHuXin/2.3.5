package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 基站扩展查询条件（固件）
 * @author Vastfy
 * @date 2022/07/08 11:35
 * @since 1.9.7
 */
@ApiModel("基站扩展查询条件（固件）")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NestExtFirmwareQueryReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "单位ID", example = "9527")
    private String unitId;

//    @ApiModelProperty(value = "基站名称", notes = "支持模糊检索", example = "测试基站")
//    private String nestName;

    @ApiModelProperty(value = "关键字（同时支持基站ID/UUID精确检索和基站名称模糊检索）", example = "测试基站")
    private String keyword;

}
