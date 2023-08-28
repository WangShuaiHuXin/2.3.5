package com.imapcloud.nest.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wmin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NestAndServerConnState {
    private Integer mqttServerConnected;
    private Integer nestConnected;
}
