package com.imapcloud.nest.v2.service.dto.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 基站信息（含固件版本信息）
 * @author Vastfy
 * @date 2022/07/08 11:12
 * @since 1.9.7
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NestExtFirmwareOutDTO extends NestBasicOutDTO {

    @ApiModelProperty(value = "基站当前固件版本信息列表", position = 100)
    private List<FirmwarePackageBasicOutDTO> curFirmwareInfos;

}
