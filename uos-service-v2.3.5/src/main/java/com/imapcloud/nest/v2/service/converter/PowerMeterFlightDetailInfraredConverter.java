package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailInfraredEntity;
import com.imapcloud.nest.v2.dao.po.out.PowerMeterFlightDetailInfraredOutDTO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterInfraredRecordOutDO;
import com.imapcloud.nest.v2.service.dto.out.PowerMeterInfraredRecordOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author wmin
 */
@Mapper(componentModel = "spring")
public interface PowerMeterFlightDetailInfraredConverter {

    PowerMeterFlightDetailInfraredConverter INSTANCES = Mappers.getMapper(PowerMeterFlightDetailInfraredConverter.class);
}
