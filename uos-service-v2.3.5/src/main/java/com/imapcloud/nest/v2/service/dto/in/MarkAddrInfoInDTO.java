package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 标注地址信息
 * @author Vastfy
 * @date 2023/02/21 09:52
 * @since 2.2.3
 */
@Data
public class MarkAddrInfoInDTO implements Serializable {

    private String markId;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String addr;

    private String addrPicPath;

}
