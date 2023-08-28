package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Vastfy
 * @date 2022/5/18 16:35
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NestAccountPageInDTO extends PageInfo {

    private Integer nestId;

    private String account;

}
