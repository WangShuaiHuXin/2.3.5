package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.TaskFileEntity;
import com.imapcloud.nest.v2.service.dto.in.DJIKmlDTO;
import com.imapcloud.nest.v2.service.dto.in.DJITaskFileInDTO;
import com.imapcloud.nest.v2.service.dto.in.DJIWpmlDTO;
import com.imapcloud.nest.v2.service.dto.out.DJITaskOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface DJITaskFileConverter {

    DJITaskFileConverter INSTANCES = Mappers.getMapper(DJITaskFileConverter.class);

    /**
     * 转换入口
     * @param in
     * @return
     */
    @Mappings({
            @Mapping(target = "taskFileId",expression = "java(String.valueOf(com.geoai.common.core.util.BizIdUtils.snowflakeId()))")
            ,@Mapping(target = "creatorId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
            ,@Mapping(target = "modifierId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
    })
    TaskFileEntity convert(DJITaskFileInDTO.DJITaskFileAddInDTO in);

    /**
     * 转换入口
     * @param in
     * @return
     */
    @Mappings({
            @Mapping(target = "modifierId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
    })
    TaskFileEntity convert(DJITaskFileInDTO.DJITaskFileUpdateInDTO in);

    /**
     * 转换出口
     * @param outDTO
     * @return
     */
    DJITaskOutDTO.DJITaskFileQueryOutDTO convert(TaskFileEntity outDTO);

    /**
     * 转换出口
     * @param djiKmlDTO
     * @return
     */
    DJIWpmlDTO convert(DJIKmlDTO djiKmlDTO);

    @Mappings({
            @Mapping(target = "waypointHeadingAngleEnable" , constant = "0")
    })
     com.imapcloud.nest.v2.service.dto.in.DJIWpmlDTO.WaypointHeadingParam waypointHeadingParamToWaypointHeadingParam(DJIKmlDTO.WaypointHeadingParam waypointHeadingParam) ;
//
    @Mappings({
            @Mapping(target = "waypointTurnDampingDist" , constant = "0")
    })
     com.imapcloud.nest.v2.service.dto.in.DJIWpmlDTO.WaypointTurnParam waypointTurnParamToWaypointTurnParam(DJIKmlDTO.WaypointTurnParam waypointTurnParam) ;

     @Mappings(
             @Mapping(target = "executeHeight", source = "height")
     )
     com.imapcloud.nest.v2.service.dto.in.DJIWpmlDTO.Placemark placemarkToPlacemark(DJIKmlDTO.Placemark placemark) ;

    @Mappings({
            @Mapping(target = "waylineId", constant = "0")
    })
     com.imapcloud.nest.v2.service.dto.in.DJIWpmlDTO.Folder folderToFolder(DJIKmlDTO.Folder folder) ;



}
