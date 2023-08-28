package com.imapcloud.nest.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class SysLogQueryParam {
    /**
     * 当前第几页
     */
    @Min(1)
    @NotNull
    private Integer pageNo;
    /**
     * 每页条数
     */
    @Max(100)
    @Min(5)
    @NotNull
    private Integer pageSize;
    /**
     * 执行时间排序，1-升序，0-降序
     */
    private Integer execTimeSort;

    /**
     * 账号模糊查询
     */
    private String account;

    /**
     * 请求IP,精准查询
     */
    private String requestIp;

    /**
     * 方法执行时间长短,1-升序，0-降序
     */
    private Long timeLenSort;

    /**
     * 方法名，模糊查询
     */
    private String methodName;

    private String startExecDate;

    private String endExecDate;
}
