package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 固件安装包信息
 * @author Vastfy
 * @date 2022/07/08 11:12
 * @since 1.9.7
 */
@ApiModel("固件安装包信息")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FirmwarePackageInfoRespVO extends FirmwarePackageBasicRespVO {

    @ApiModelProperty(value = "安装包版本描述", position = 5, required = true, example = "1")
    private String versionDesc;

    @ApiModelProperty(value = "无人机标识", position = 6, required = true, example = "0")
    private Integer uavWhich;


}
