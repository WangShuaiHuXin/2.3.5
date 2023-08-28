package com.imapcloud.nest.v2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.v2.service.dto.out.MqttBrokerInfoOutDTO;

import java.util.List;

/**
 * <p>
 * mqtt代理信息表 服务类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
public interface BaseMqttBrokerService {

    List<MqttBrokerInfoOutDTO> listMqttBrokerInfos(List<String> mqttBrokerIdList);

}
