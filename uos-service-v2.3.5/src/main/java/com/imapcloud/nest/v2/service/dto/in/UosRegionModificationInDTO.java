package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * @Classname RegionModificationInDTO
 * @Description 区域修改信息
 * @Date 2022/8/11 10:49
 * @Author Carnival
 */

@Data
public class UosRegionModificationInDTO {

    private String regionId;

    private String regionName;

    private String description;
}
