package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 单位角色检索条件
 * @author Vastfy
 * @date 2022/08/12 14:12
 * @since 2.0.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UosRoleQueryDTO extends PageInfo {

    /**
     * 角色类型
     */
    private Integer roleType;

    /**
     * 应用类型
     */
    private String appType;

    /**
     * 应用类型前缀
     */
    private String appTypePrefix;;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 单位编码
     */
    private String orgCode;

}
