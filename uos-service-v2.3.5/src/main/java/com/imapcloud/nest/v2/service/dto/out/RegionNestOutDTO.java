package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 区域维度分组展示的基站信息
 * @author Vastfy
 * @date 2022/07/08 11:12
 * @since 1.9.7
 */
@Data
public class RegionNestOutDTO implements Serializable {

    /**
     * 区域ID
     */
    private String regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 基站基本信息列表
     */
    private List<NestBasicOutDTO> nestInfos;

}
