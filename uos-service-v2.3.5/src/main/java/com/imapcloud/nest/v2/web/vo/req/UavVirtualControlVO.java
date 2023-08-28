package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.LimitVal;
import com.imapcloud.nest.common.annotation.NestId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@ApiModel("无人机控制条件")
@Data
public class UavVirtualControlVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<UavControlVO> uavControlVOList;

    /**
     * 俯仰
     */
    @Min(-15)
    @Max(15)
    private Float pitch;
    /**
     * 横滚
     */
    @Min(-15)
    @Max(15)
    private Float roll;
    /**
     * 偏航
     */
    @Min(-200)
    @Max(200)
    private Float yaw;
    /**
     * 油门
     */
    @Min(-4)
    @Max(4)
    private Float throttle;
}
