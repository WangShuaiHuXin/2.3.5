package com.imapcloud.nest.v2.service.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PowerInspcetionReportConverter {

        PowerInspcetionReportConverter INSTANCE = Mappers.getMapper(PowerInspcetionReportConverter.class);


}
