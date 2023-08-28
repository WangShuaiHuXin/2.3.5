package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import scala.Int;

/**
 * 表计数据查询请求信息
 * @author Vastfy
 * @date 2022/11/29 10:59
 * @since 2.1.5
 */
@ApiModel("表计数据详情查询请求信息")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MeterDataDetailQueryReqVO extends PageInfo {

    @ApiModelProperty(value = "表计数据ID", position = 1, example = "95279666", hidden = true)
    private String dataId;

    @ApiModelProperty(value = "设备状态【取字典`GEOAI_DIAL_DEVICE_STATE`数据项值】", position = 2, example = "1")
    private Integer deviceState;

    @ApiModelProperty(value = "读数状态【取字典`GEOAI_DIAL_READING_STATE`数据项值】", position = 3, example = "2")
    private Integer readingState;

    @ApiModelProperty(value = "核实状态  0 待处理   1已核实    2 误报")
    private Integer verificationStatus;
}
