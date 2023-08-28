package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDataEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailEntity;
import com.imapcloud.nest.v2.service.dto.out.MeterDataDetailInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDataInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerHomeSettingOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.PowerHomeBaseSettingRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 电力数据转换器
 *
 * @author Vastfy
 * @date 2022/12/04 16:09
 * @since 2.1.5
 */
@Mapper(componentModel = "spring")
public interface PowerDataConverter {

    PowerDataConverter INSTANCE = Mappers.getMapper(PowerDataConverter.class);

    MeterDataInfoOutDTO convert(PowerMeterFlightDataEntity in);

    MeterDataDetailInfoOutDTO convert(PowerMeterFlightDetailEntity in);

    @Mappings({
            @Mapping(source = "inspectionPoints", target = "inspectPoint"),
            @Mapping(source = "generalInspection", target = "totalTimes"),
            @Mapping(source = "todayInspection", target = "todayTimes"),
            @Mapping(source = "cumulativePhotography", target = "cumulativePhoto"),
            @Mapping(source = "alarmStatisticsProcessed", target = "alarmProcessed"),
            @Mapping(source = "alarmStatisticsPending", target = "alarmPending")
    }
    )
    PowerHomeBaseSettingRespVO convert(PowerHomeSettingOutDTO in);

}
