package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.BaseNestEntity;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestQueryOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BaseNestConverter {
    BaseNestConverter INSTANCES = Mappers.getMapper(BaseNestConverter.class);

    @Mappings({
            @Mapping(source = "uuid", target = "nestUuid"),
            @Mapping(source = "name", target = "nestName"),
            @Mapping(source = "type", target = "nestType")
    })
    NestQueryOutDTO convertNestQueryOutDTO(BaseNestEntity ent);

    BaseNestInfoOutDTO convertNestInfoOutDTO(BaseNestEntity ent);
}
