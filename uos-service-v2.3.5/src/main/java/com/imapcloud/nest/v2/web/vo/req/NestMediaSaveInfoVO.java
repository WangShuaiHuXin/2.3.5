package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 基站流媒体保存信息
 * @author Vastfy
 * @date 2023/3/30 19:47
 * @since 2.3.2
 */
@ApiModel("基站流媒体保存信息")
@Data
public class NestMediaSaveInfoVO {


    @ApiModelProperty(value = "巢内监控设备ID", position = 1, example = "XXX")
    private String insideDeviceCode;

    @ApiModelProperty(value = "巢外监控设备ID", position = 2, example = "XXX")
    private String outsideDeviceCode;

    @ApiModelProperty(value = "无人机推流地址", position = 3, example = "XXX")
    private List<UavMeta> uavMetaList;

    @ApiModelProperty(value = "监控serverId", position = 4, example = "XXX")
    @Deprecated
    private String monitorServerId;

    @ApiModelProperty(value = "大疆streamId", position = 5, example = "XXX")
    private String djiMonitorStreamId;
    /**
     * 无人机
     */
    @Data
    public static class UavMeta {

        private String uavId;

        @Deprecated
        private String serverId;

        private String streamId;
    }

}
