package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 固件版本查询条件
 * @author Vastfy
 * @date 2022/07/08 11:35
 * @since 1.9.7
 */
@ApiModel("固件版本查询条件")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FirmwareVersionQueryReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "更新包类型【1：CPS固件；2：MPS固件】", position = 1, example = "1")
    private Integer type;

    @ApiModelProperty(value = "更新包名称（支持模糊检索）", position = 2, example = "测试9527基站.apk")
    private String apkName;

    @ApiModelProperty(value = "无人机标识", position = 3, example = "0")
    private Integer uavWhich;

}
