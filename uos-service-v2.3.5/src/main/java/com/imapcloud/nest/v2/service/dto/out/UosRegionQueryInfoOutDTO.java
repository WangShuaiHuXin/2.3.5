package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Classname RegionQueryOutDTO
 * @Description 查询区域详细信息
 * @DATE 2022/8/11 18:19
 * @Author Carnival
 */
@Data
public class UosRegionQueryInfoOutDTO implements Serializable {

    private String regionId;

    private String regionName;

    private String description;

    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;
}
