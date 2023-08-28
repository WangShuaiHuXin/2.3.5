package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

/**
 * 账户和单位ID信息
 *
 * @author boluo
 * @date 2022-05-27
 */
@Data
public class AccountAndUnitOutDTO {

    /**
     * 单位自增id
     */
    private Long unitId;

    /**
     * 账号id
     */
    private Long accountId;
}
