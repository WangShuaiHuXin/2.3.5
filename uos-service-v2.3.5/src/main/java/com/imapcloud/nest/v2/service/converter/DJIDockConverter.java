package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.service.dto.out.DJIDockInfoOutDTO;
import com.imapcloud.sdk.pojo.djido.DjiDockPropertyOsdDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface DJIDockConverter {

    DJIDockConverter INSTANCES = Mappers.getMapper(DJIDockConverter.class);

    /**
     * 转换入口
     * @param in
     * @return
     */
    @Mappings({
            @Mapping(target = "droneCharge", source = "in.droneChargeState.state"),
            @Mapping(target = "droneCapacityPercent", source = "droneChargeState.capacityPercent")
    })
    DJIDockInfoOutDTO convert(DjiDockPropertyOsdDO in);

}
