package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname UosMqttPageInDTO
 * @Description Mqtt代理地址分页查询信息
 * @Date 2022/8/16 15:02
 * @Author Carnival
 */
@Data
public class UosMqttPageInDTO extends PageInfo implements Serializable {

    private String mqttName;
}
