package com.imapcloud.nest.sdk.converter;

import com.imapcloud.sdk.pojo.djido.DjiDockLiveCapacityStateDO;
import com.imapcloud.sdk.pojo.djido.DjiDockLiveStateDO;
import com.imapcloud.sdk.pojo.djido.DjiDockPropertyStateDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface DjiLiveStateConverter {
    DjiLiveStateConverter INSTANCES = Mappers.getMapper(DjiLiveStateConverter.class);

    void dockStateDOUpdateAFromB(DjiDockPropertyStateDO a, @MappingTarget DjiDockLiveCapacityStateDO b);

    void dockStateDOUpdateAFromB(DjiDockPropertyStateDO a, @MappingTarget DjiDockLiveStateDO b);


}
