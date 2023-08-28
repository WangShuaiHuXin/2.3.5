package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 账号响应视图对象示例
 * @author Vastfy
 * @date 2022/4/25 15:12
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AccountInfoInDTO extends PageInfo implements Serializable {

    private String account;

    private String mobile;

    private Integer status;

    private String unitId;

}
