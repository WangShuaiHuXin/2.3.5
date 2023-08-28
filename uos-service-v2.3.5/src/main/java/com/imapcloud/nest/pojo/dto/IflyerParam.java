package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by wmin on 2020/12/16 16:46
 *
 * @author wmin
 */
@Data
public class IflyerParam {

    /**
     * 当前页
     */
    @Min(1)
    private Integer currPage;

    /**
     * 每页条数
     */
    @Min(1)
    @Max(20)
    private Integer pageSize;

    /**
     * 单位Id
     */
    @NotNull(message = "{geoai_uos_cannot_empty_unitid}")
    private String unitId;

    /**
     * 任务类型Id
     */
    private Integer taskType;

    /**
     * 查询关键字
     */
    private String keyword;


}
