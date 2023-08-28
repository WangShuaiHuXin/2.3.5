package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName SysOrgPilotSpaceOutDTO.java
 * @Description SysOrgPilotSpaceOutDTO
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysOrgPilotSpaceRespVO {

    /**
     * 单位编码
     */
    @ApiModelProperty(value = "组织编码", position = 1, required = true, example = "10000")
    private String orgCode;

    /**
     * pilot工作空间
     */
    @ApiModelProperty(value = "工作空间", position = 2, required = true, example = "10000")
    private String workSpaceId;

    /**
     * 组织名称
     */
    @ApiModelProperty(value = "组织名字", position = 3, required = true, example = "10000")
    private String orgName;


}
