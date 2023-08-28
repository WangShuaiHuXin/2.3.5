package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.BaseUavNestRefEntity;
import com.imapcloud.nest.v2.service.dto.out.BaseNestUavOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BaseUavNestConverter {
    BaseUavNestConverter INSTANCES = Mappers.getMapper(BaseUavNestConverter.class);

    BaseNestUavOutDTO convert(BaseUavNestRefEntity entity);
}
