package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

@Data
public class UosRegionInfoOutDTO {
    /**
     * 区域业务ID
     */
    private String regionId;

    /**
     * 区域名称
     */
    private String regionName;
}
