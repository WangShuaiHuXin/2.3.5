package com.imapcloud.nest.pojo.vo.req;

import lombok.Data;

/**
 * 备降功能表
 *
 * @author boluo
 * @date 2022-08-30
 */
@Data
public class BackLandFunReqVO {

    /**
     * 备降点名称
     */
    private String name;

    private String backLandFunId;

    /**
     * 基站id
     */
    private String nestId;

    /**
     * 默认备降点经度
     */
    private Double backLandPointLng;

    /**
     * 默认备降点纬度
     */
    private Double backLandPointLat;

    /**
     * 飞往默认备降点相对高度
     */
    private Double gotoBackLandPointAlt;
}
