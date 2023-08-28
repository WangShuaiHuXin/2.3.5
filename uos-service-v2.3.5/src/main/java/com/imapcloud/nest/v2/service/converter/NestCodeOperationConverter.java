package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.NestCodeOperationRecordsEntity;
import com.imapcloud.nest.v2.service.dto.in.NestCodeOperationInDTO;
import com.imapcloud.nest.v2.service.dto.out.NestCodeOperationOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NestCodeOperationConverter {
    NestCodeOperationConverter INSTANCES = Mappers.getMapper(NestCodeOperationConverter.class);

    NestCodeOperationRecordsEntity convert(NestCodeOperationInDTO dto);

    NestCodeOperationOutDTO convertEnt2OutDTO(NestCodeOperationRecordsEntity entity);
}
