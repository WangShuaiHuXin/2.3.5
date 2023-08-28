package com.imapcloud.nest.v2.service;


import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.UosMqttCreationInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosMqttModifyInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosMqttPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosMqttQueryOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosMqttSimpleOutDTO;

import java.util.List;


/**
 * @Classname UosMqttService
 * @Description Mqtt代理地址接口定义
 * @Date 2022/8/16 14:42
 * @Author Carnival
 */
public interface UosMqttService {

    /**
     * 新建mqtt代理地址
     */
    String addMqtt(UosMqttCreationInDTO mqttCreationInDTO);

    /**
     * 删除mqtt代理地址
     */
    Boolean deleteMqtt(String mqttBrokerId);

    /**
     * 修改mqtt代理地址信息
     */
    Boolean modifyMqttInfo(String mqttBrokerId, UosMqttModifyInDTO mqttModifyInDTO);

    /**
     * 查询mqtt代理地址信息
     */
    UosMqttQueryOutDTO queryMqttInfo(String mqttBrokerId);

    /**
     * 分页查询mqtt地址列表
     */
    PageResultInfo<UosMqttQueryOutDTO> pageMqttList(UosMqttPageInDTO mqttPageInDTO);

    /**
     * 获取Mqtt代理地址简要信息
     */
    List<UosMqttSimpleOutDTO> listMqttSimpleInfo();

    /**
     * 分页获取Mqtt代理地址简要信息
     */
    PageResultInfo<UosMqttSimpleOutDTO> pageMqttSimpleInfo(UosMqttPageInDTO uosMqttPageInDTO);

    /**
     * 重置mqtt客户端
     *
     * @param nestId 巢id
     */
    void clientReset(String nestId);

    /**
     * 客户机重置uuid
     *
     * @param uuid uuid
     */
    void clientResetByUuid(String uuid);
}
