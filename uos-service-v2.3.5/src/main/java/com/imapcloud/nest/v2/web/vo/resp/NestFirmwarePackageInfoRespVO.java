package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 基站固件安装包更新记录
 * @author Vastfy
 * @date 2022/07/08 11:12
 * @since 1.9.7
 */
@ApiModel("基站固件安装包更新记录")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NestFirmwarePackageInfoRespVO extends FirmwarePackageInfoRespVO {

    @ApiModelProperty(value = "基站编号", position = 100, required = true, example = "9527001")
    private String nestId;

    @ApiModelProperty(value = "更新时间", position = 101, required = true, example = "2022-07-14 10:10:11")
    private LocalDateTime modifiedTime;

    @ApiModelProperty(value = "更新状态【-1：安装超时；0：安装中；1：安装成功；2：安装失败】", position = 102, required = true, example = "1")
    private Integer state;

}
