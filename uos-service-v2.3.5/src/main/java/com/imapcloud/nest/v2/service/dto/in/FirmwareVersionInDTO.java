package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 固件版本查询条件
 * @author Vastfy
 * @date 2022/07/13 10:35
 * @since 1.9.7
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FirmwareVersionInDTO extends PageInfo {

    /**
     * 更新包类型【1：CPS固件；2：MPS固件】
     */
    private Integer type;

    /**
     * 更新包名称（支持模糊检索）
     */
    private String apkName;

    /**
     * 无人机标识
     */
    private Integer uavWhich;

}
