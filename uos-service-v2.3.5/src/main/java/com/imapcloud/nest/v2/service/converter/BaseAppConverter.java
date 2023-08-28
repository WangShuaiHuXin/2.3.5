package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.BaseAppEntity;
import com.imapcloud.nest.v2.service.dto.in.SaveBaseAppInDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BaseAppConverter {
    BaseAppConverter INSTANCES = Mappers.getMapper(BaseAppConverter.class);

}
