package com.imapcloud.nest.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统标签表
 * </p>
 *
 * @author wmin
 * @since 2021-02-23
 */
@Data
public class SysLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行的方法名
     */
    private String methodName;

    /**
     * 执行的方法参数
     */
    private String methodParam;

    /**
     * 执行时长，单位毫秒
     */
    private Long timeLength;

    /**
     * 执行时间
     */
    private LocalDateTime execTime;


    /**
     * 执行日期
     */
    private Long execDateMilli;


    /**
     * 日志的备注
     */
    private String remarks;

    /**
     * 异常信息
     */
    private String exception;

    /**
     * 请求IP
     */
    private String requestIp;

    /**
     * 请求用户
     */
    private String requestAccount;

    /**
     * @deprecated at 2.0.0，使用{@link SysLogEntity#orgCode}替代
     */
    @Deprecated
    private Integer unitId;

    /**
     * 单位编码
     */
    private String orgCode;

    private Object result;

}
