package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 国标设备查询条件
 * @author Vastfy
 * @date 2023/03/30 15:12
 * @since 2.3.2
 */
@ApiModel("国标设备查询条件")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DeviceQueryReqVO extends PageInfo {

    @ApiModelProperty(value = "设备名称（支持全文检索）", position = 1, example = "佛山G503-14巢内")
    private String name;

    @ApiModelProperty(value = "设备编码（精确检索）", position = 2)
    private String deviceCode;

    @ApiModelProperty(value = "基站ID", position = 3)
    private String nestId;

}
