package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PowerDiscernFunSettingInfosOutDO {
    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 识别类型【取字典编码`GEOAI_POWER_DISCERN_TYPE`数据项值】
     */
    private Integer discernType;

    /**
     * 识别功能ID
     */
    private String discernFunId;

    private String lastModifierId;

    private LocalDateTime lastModifiedTime;

}
