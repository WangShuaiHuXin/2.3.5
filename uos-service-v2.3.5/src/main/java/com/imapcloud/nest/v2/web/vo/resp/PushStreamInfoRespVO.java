package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 普通推流信息
 * @author Vastfy
 * @date 2023/04/06 15:12
 * @since 1.0.0
 */
@ApiModel("普通推流信息")
@Data
public class PushStreamInfoRespVO implements Serializable {

    @ApiModelProperty(value = "推流ID", position = 1, example = "A9527")
    private String streamId;

    @ApiModelProperty(value = "流媒体服务ID", position = 2, example = "B4401")
    private String serverId;

    @ApiModelProperty(value = "推流地址", position = 3, example = "B4401")
    private String pushUrl;

    @ApiModelProperty(value = "拉流地址，仅历史数据才有该值", position = 4, example = "xxx")
    private String pullUrl;

}
