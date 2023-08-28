package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;

/**
 * CPS同步基站日志信息
 * @author Vastfy
 * @date 2023/2/23 14:58
 * @since 2.3.0
 */
@Data
public class CpsSyncNestLogInDTO implements Serializable {

    private String nestUuid;

    private Integer which;

    private String logFilename;

    private String storageUrl;

    private String module;

}
