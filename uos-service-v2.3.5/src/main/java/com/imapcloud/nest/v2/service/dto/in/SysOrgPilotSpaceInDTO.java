package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName SysOrgPilotSpaceInDTO.java
 * @Description SysOrgPilotSpaceInDTO
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysOrgPilotSpaceInDTO {

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * pilot工作空间
     */
    private String workSpaceId;

    /**
     * 工作空间名称
     */
    private String orgName;

}
