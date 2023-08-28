package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
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
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NestExtFirmwareQueryInDTO extends PageInfo implements Serializable {

    /**
     * 单位ID
     */
    private String unitId;
//
//    /**
//     * 基站名称，支持模糊检索
//     */
//    private String nestName;

    /**
     * 关键字（同时支持基站ID/UUID精确检索和基站名称模糊检索）
     */
    private String keyword;


    @ApiModelProperty(value = "设备用途", example = "1")
    private Integer deviceUse;

}
