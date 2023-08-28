package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI自动识别配置信息
 * @author Vastfy
 * @date 2022/11/28 13:53
 * @since 2.1.5
 */
@ApiModel("AI自动识别配置信息")
@Data
public class AIAutoDiscernSettingsReqVO implements Serializable {

    @ApiModelProperty(value = "是否开启自动AI识别", required = true, position = 1, example = "true")
    private boolean enabled;

    @ApiModelProperty(value = "最后修改人ID", position = 2, example = "9527666")
    private String lastModifierId;

    @ApiModelProperty(value = "最后修改人名称", position = 3, example = "张三")
    private String lastModifierName;

    @ApiModelProperty(value = "最后修改时间", position = 4, example = "2022-11-28 00:00:00")
    private LocalDateTime lastModifiedTime;

}
