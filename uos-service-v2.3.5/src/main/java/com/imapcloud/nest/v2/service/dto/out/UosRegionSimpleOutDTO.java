package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * @Classname UosRegionBasicOutDTO
 * @Description 区域简要信息
 * @Date 2022/8/26 10:20
 * @Author Carnival
 */
@Data
public class UosRegionSimpleOutDTO implements Serializable {

    private String regionId;

    private String regionName;
}
