package com.imapcloud.nest.v2.manager.dataobj.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Classname DeviceInfoOutDO
 * @Description 设备信息
 * @Date 2023/4/6 18:23
 * @Author Carnival
 */
@Data
public class DeviceInfoOutDO {

    @ApiModelProperty(value = "设备编码", position = 1, required = true, example = "10000A")
    private String code;

    @ApiModelProperty(value = "设备名称", position = 2, required = true, example = "佛山G503-14巢内监控")
    private String name;

    @ApiModelProperty(value = "通道名称", position = 3, required = true, example = "10000A")
    private String channelCode;

    @ApiModelProperty(value = "注册IP", position = 4, example = "192.168.0.111")
    private String ip;

    @ApiModelProperty(value = "注册端口", position = 5, example = "9195")
    private Integer port;

    @ApiModelProperty(value = "制造商", position = 6, example = "海康威视")
    private String manufacturer;

    @ApiModelProperty(value = "传输模式【取字典`DEVICE_TRANSPORT_MODE`数据项值】", position = 7, required = true, example = "1")
    private Integer transportMode;

    @ApiModelProperty(value = "设备状态【取字典`DEVICE_STATE`数据项值】", position = 8, required = true, example = "-1")
    private Integer deviceState;

    @ApiModelProperty(value = "上次心跳时间", position = 9, required = true, example = "2023-03-30 16:59:22")
    private LocalDateTime lastHeartbeat;

    @ApiModelProperty(value = "上次注册时间", position = 10, required = true, example = "2023-03-30 16:59:22")
    private LocalDateTime lastRegistration;

}
