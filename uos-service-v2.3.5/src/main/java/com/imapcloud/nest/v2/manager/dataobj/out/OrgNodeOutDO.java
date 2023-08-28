package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 单位节点信息
 * @author Vastfy
 * @date 2022/07/25 15:06
 * @since 1.0.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OrgNodeOutDO extends OrgSimpleOutDO {

    private String orgCode;

    private String orgName;

    private Double longitude;

    private Double latitude;

    private String parentCode;

    private List<OrgNodeOutDO> children;

}
