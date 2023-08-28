package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.MediaStreamEntity;
import com.imapcloud.nest.v2.service.dto.in.SaveStreamInDTO;
import com.imapcloud.nest.v2.service.dto.out.MediaStreamOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MediaStreamConverter {
    MediaStreamConverter INSTANCES = Mappers.getMapper(MediaStreamConverter.class);

    MediaStreamOutDTO convert(MediaStreamEntity entity);

    MediaStreamEntity convert(SaveStreamInDTO dto);
}
