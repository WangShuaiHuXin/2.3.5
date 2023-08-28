package com.imapcloud.nest.convert;

import com.imapcloud.nest.common.convert.intf.Translator;
import com.imapcloud.nest.model.FlightMissionEntity;
import com.imapcloud.nest.pojo.vo.FlightMissionVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName FlightMissionVOToEntityConvert.java
 * @Description FlightMissionVOToEntityConvert
 * @createTime 2022年03月24日 16:34:00
 */
@Mapper
public interface FlightMissionVOToEntityConvert extends Translator<FlightMissionVO, FlightMissionEntity> {

    FlightMissionVOToEntityConvert INSTANCES = Mappers.getMapper(FlightMissionVOToEntityConvert.class);

    @Override
    List<FlightMissionVO> doToDto(List<FlightMissionEntity> flightMissionEntityList);

    @Override
    List<FlightMissionEntity> dtoToDo(List<FlightMissionVO> flightMissionVOList);

    /**
     *  更新实体
     * @param flightMissionVO
     * @param flightMissionEntity
     */
    @Mappings({

    })
    void updateFlightMissionEntity(FlightMissionVO flightMissionVO, @MappingTarget FlightMissionEntity flightMissionEntity);

    @Override
    @Mappings({
//            @Mapping(target = "createTime", ignore = true),
//            @Mapping(source = "execId",target = "execMissionID")
    })
    FlightMissionVO doToDto(FlightMissionEntity flightMissionEntity);

    @Override
    @Mappings({
//            @Mapping(target = "createTime", ignore = true),
//            @Mapping(source = "execMissionID",target = "execId")
    })
    FlightMissionEntity dtoToDo(FlightMissionVO flightMissionVO);

}

