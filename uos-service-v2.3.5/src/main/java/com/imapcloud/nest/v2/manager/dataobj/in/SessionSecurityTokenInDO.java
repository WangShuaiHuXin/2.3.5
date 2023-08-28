package com.imapcloud.nest.v2.manager.dataobj.in;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 会话安全凭证生成信息
 * @author double
 * @date 2023/01/31 15:12
 * @since 1.0.0
 */
@Data
public class SessionSecurityTokenInDO implements Serializable {

    private Integer expiry;

    private Boolean outerEndpoint;

}
