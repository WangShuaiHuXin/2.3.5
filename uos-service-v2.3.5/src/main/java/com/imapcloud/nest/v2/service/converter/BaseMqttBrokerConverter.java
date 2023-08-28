package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.BaseMqttBrokerEntity;
import com.imapcloud.nest.v2.service.dto.out.MqttBrokerInfoOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BaseMqttBrokerConverter {

    BaseMqttBrokerConverter INSTANCES = Mappers.getMapper(BaseMqttBrokerConverter.class);

    @Mappings({
            @Mapping(source = "innerDomain", target = "mqttBrokerInnerUrl"),
            @Mapping(source = "outerDomain", target = "mqttBrokerOuterUrl"),
            @Mapping(source = "account", target = "username")
    })
    MqttBrokerInfoOutDTO convert(BaseMqttBrokerEntity baseMqttBrokerEntity);
}
