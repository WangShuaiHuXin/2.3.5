package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 单位简要信息
 * @author Vastfy
 * @date 2022/8/18 13:47
 * @since 2.0.0
 */
@Data
public class OrgSimpleOutDO implements Serializable {

    private String orgCode;

    private String orgName;

    private Double longitude;

    private Double latitude;

    private String parentOrgCode;

}
