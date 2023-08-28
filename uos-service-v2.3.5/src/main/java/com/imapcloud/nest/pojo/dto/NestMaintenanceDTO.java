package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wmin
 */
@Data
public class NestMaintenanceDTO {
    /**
     * 维保记录id
     */
    private Integer id;

    @NotNull
    private String nestId;
    /**
     * 开始维保时间戳
     */
    @NotNull
    private String startTime;

    /**
     * 结束时间戳
     */
    @NotNull
    private String endTime;

    /**
     * 维保类型
     */
    @NotNull
    private Integer type;

//    @NotNull
//    private List<Integer> projectIdList;

    /**
     * 维保项目id
     */
    @NotNull
    private String project;

    /**
     * 维保人员
     */
    @NotNull
    private String staff;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 电池序号
     */
    private String batteryIndex;

    /**
     * 保养字段名
     */
    private String maintainName;

    /**
     * 附件地址名
     */
    private String attachmentPath;
}
