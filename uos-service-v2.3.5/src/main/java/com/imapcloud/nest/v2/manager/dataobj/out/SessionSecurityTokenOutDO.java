package com.imapcloud.nest.v2.manager.dataobj.out;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 临时安全令牌信息
 * @author double
 * @date 2023/3/17 17:18
 * @since 1.0.0
 */
@Data
public class SessionSecurityTokenOutDO {

    private String accessKey;

    private String secretKey;

    private String sessionToken;

    private Integer expiry;

    private String endpoint;

}
