package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 基站扩展查询条件（基站监控）
 * @author Vastfy
 * @date 2023/04/19 19:35
 * @since 2.3.2
 */
@ApiModel("基站扩展查询条件（基站监控）")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NestExtMonitorQueryReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "设备用途", example = "1")
    private Integer deviceUse;

}
