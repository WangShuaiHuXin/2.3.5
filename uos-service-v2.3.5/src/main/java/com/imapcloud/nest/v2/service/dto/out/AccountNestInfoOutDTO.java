package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 基站简要信息
 *
 * @author Vastfy
 * @date 2022/8/25 16:47
 * @since 2.0.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountNestInfoOutDTO extends NestSimpleOutDTO {

    /**
     * 是否控制基站
     */
    private boolean grantControl;

}
