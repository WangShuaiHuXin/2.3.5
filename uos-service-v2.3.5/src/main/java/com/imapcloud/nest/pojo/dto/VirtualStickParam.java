package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Data
public class VirtualStickParam {

    @NestId(more = true)
    private List<String> nestIdList;
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
