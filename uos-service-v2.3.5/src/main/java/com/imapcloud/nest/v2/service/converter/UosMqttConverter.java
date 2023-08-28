package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.UosMqttEntity;
import com.imapcloud.nest.v2.service.dto.out.UosMqttQueryOutDTO;
import org.mapstruct.Mapper;

/**
 * @Classname UosMqttConverter
 * @Description Mqtt代理地址转换类
 * @Date 2022/8/16 15:11
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface UosMqttConverter {

    UosMqttQueryOutDTO transform(UosMqttEntity in);

}
