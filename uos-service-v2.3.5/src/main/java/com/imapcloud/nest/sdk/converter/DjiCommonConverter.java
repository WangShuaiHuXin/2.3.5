package com.imapcloud.nest.sdk.converter;

import com.imapcloud.sdk.pojo.djido.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DjiCommonConverter {
    DjiCommonConverter INSTANCES = Mappers.getMapper(DjiCommonConverter.class);

    /**
     * @param a
     * @param b
     */
    @Mapping(target = "data", ignore = true)
    void dockOsdDOUpdateAFromB(DjiCommonDO<DjiDockPropertyOsdDO> a, @MappingTarget DjiCommonDO<DjiDockPropertyOsdDO> b);

    /**
     * @param a
     * @param b
     */
    @Mapping(target = "data", ignore = true)
    void pilotOsdDOUpdateAFromB(DjiCommonDO<DjiPilotPropertyOsdDO> a, @MappingTarget DjiCommonDO<DjiPilotPropertyOsdDO> b);

    @Mapping(target = "data", ignore = true)
    void dockStateDOUpdateAFromB(DjiCommonDO<DjiDockPropertyStateDO> a, @MappingTarget DjiCommonDO<DjiDockPropertyStateDO> b);

    void dockStateDOUpdateAFromB(DjiDockPropertyStateDO a, @MappingTarget DjiDockPropertyStateDO b);

    void dockOsdDOUpdateAFromB(DjiDockPropertyOsdDO a, @MappingTarget DjiDockPropertyOsdDO b);

    void pilotOsdDOUpdateAFromB(DjiPilotPropertyOsdDO a, @MappingTarget DjiPilotPropertyOsdDO b);

    @Mapping(target = "data", ignore = true)
    void uavOsdDOUpdateAFromB(DjiCommonDO<DjiUavPropertyOsdDO> a, @MappingTarget DjiCommonDO<DjiUavPropertyOsdDO> b);

    void uavOsdDOUpdateAFromB(DjiUavPropertyOsdDO a, @MappingTarget DjiUavPropertyOsdDO b);

    void flightTaskProgressDOUpdateAFromB(DjiCommonDO<FlightTaskProgressDO> a, @MappingTarget DjiCommonDO<FlightTaskProgressDO> b);


}
