package com.imapcloud.nest.convert;

import com.imapcloud.nest.common.convert.intf.Translator;
import com.imapcloud.nest.model.FlightMissionEntity;
import com.imapcloud.nest.pojo.vo.FlightMissionExportVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName FlightMissionExportVoToEntityConvert.java
 * @Description FlightMissionExportVoToEntityConvert
 * @createTime 2022年03月24日 16:34:00
 */
@Mapper
public interface FlightMissionExportVoToEntityConvert extends Translator<FlightMissionExportVO, FlightMissionEntity> {

    FlightMissionExportVoToEntityConvert INSTANCES = Mappers.getMapper(FlightMissionExportVoToEntityConvert.class);

    /**
     *  批量转换Entity到VO
     * @param flightMissionEntityList
     * @return
     */
    @Override
    List<FlightMissionExportVO> doToDto(List<FlightMissionEntity> flightMissionEntityList);

    /**
     *  批量转换VO 到Entity
     * @param flightMissionExportVOList
     * @return
     */
    @Override
    List<FlightMissionEntity> dtoToDo(List<FlightMissionExportVO> flightMissionExportVOList);

    /**
     *  更新实体
     * @param flightMissionExportVO
     * @param flightMissionEntity
     */
    @Mappings({

    })
    void updateFlightMissionEntity(FlightMissionExportVO flightMissionExportVO, @MappingTarget FlightMissionEntity flightMissionEntity);

    /**
     *  转换Enttiy 到VO
     * @param flightMissionEntity
     * @return
     */
    @Override
    @Mappings({
//            @Mapping(target = "createTime", ignore = true),
//            @Mapping(source = "execId",target = "execMissionID")
    })
    FlightMissionExportVO doToDto(FlightMissionEntity flightMissionEntity);

    /**
     * 转换VO 到 Entity
     * @param flightMissionExportVO
     * @return
     */
    @Override
    @Mappings({
//            @Mapping(target = "createTime", ignore = true),
//            @Mapping(source = "execMissionID",target = "execId")
    })
    FlightMissionEntity dtoToDo(FlightMissionExportVO flightMissionExportVO);

}

