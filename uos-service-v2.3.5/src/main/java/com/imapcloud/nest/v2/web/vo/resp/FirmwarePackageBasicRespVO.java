package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 固件安装包基本信息
 * @author Vastfy
 * @date 2022/07/08 11:12
 * @since 1.9.7
 */
@ApiModel("固件安装包基本信息")
@Data
public class FirmwarePackageBasicRespVO implements Serializable {

    @ApiModelProperty(value = "安装包ID", position = 1, required = true, example = "9527")
    private String id;

    @ApiModelProperty(value = "安装包名称", position = 2, required = true, example = "EACC_G600_v2.0.052701_t1_release_20220530.apk")
    private String name;

    @ApiModelProperty(value = "安装包类型【1：CPS；2：MPS】", position = 3, required = true, example = "1")
    private String type;

    @ApiModelProperty(value = "安装包版本号", position = 4, required = true, example = "v2.0.052701")
    private String version;

    @ApiModelProperty(value = "无人机标识", position = 5, required = true, example = "0")
    private Integer uavWhich;

}
