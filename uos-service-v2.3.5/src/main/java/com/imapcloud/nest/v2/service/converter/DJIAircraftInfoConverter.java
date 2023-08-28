package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.service.dto.out.DJIAircraftInfoOutDTO;
import com.imapcloud.sdk.pojo.djido.DjiUavPropertyOsdDO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring" , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DJIAircraftInfoConverter {

    DJIAircraftInfoConverter INSTANCES = Mappers.getMapper(DJIAircraftInfoConverter.class);

    /**
     * 转换入口
     * @param in
     * @return
     */
    DJIAircraftInfoOutDTO convert(DjiUavPropertyOsdDO in);

    @Mappings({
            @Mapping(target = "distanceLimit",source = "distanceLimitStatus.distanceLimit")
    })
    void updateDJIAircraftInfoOutDTO( DjiUavPropertyOsdDO in ,@MappingTarget DJIAircraftInfoOutDTO djiAircraftInfoOutDTO);

}
