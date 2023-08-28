package com.imapcloud.nest.v2.service.dto.in;

import com.imapcloud.nest.common.annotation.LimitVal;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wmin
 */
@Data
public class ChargeLiveLensInDTO {
    /**
     * 基站id
     */
    @NotNull
    private String nestId;
    /**
     * 直播视频流镜头类型，0-默认，1-广角，2-变焦，3-红外
     */
    @LimitVal(values = {"0,1,2,3"}, message = "videoType值不在范围")
    private Integer videoType;
}
