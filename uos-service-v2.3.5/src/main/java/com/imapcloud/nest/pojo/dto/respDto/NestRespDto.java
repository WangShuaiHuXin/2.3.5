package com.imapcloud.nest.pojo.dto.respDto;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
public class NestRespDto {

    private static final long serialVersionUID=1L;

    /**
     * 流的url
     */
    private String flowUrl;

    //展示监控的状态
    private Integer showStatus;

    //流的类型
    private Integer flowType;

}
