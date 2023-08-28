package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * 区域
 *
 * @author boluo
 * @date 2022-08-26
 */
@Data
public class UosRegionOutDO {

    /**
     * 区域业务ID
     */
    private String regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 区域描述
     */
    private String description;
}
