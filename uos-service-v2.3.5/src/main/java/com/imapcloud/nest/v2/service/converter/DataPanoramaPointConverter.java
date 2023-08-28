package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.DataPanoramaPointEntity;
import com.imapcloud.nest.v2.service.dto.in.DataPanoramaPointInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaPointOutDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointConverter.java
 * @Description DataPanoramaPointConverter
 * @createTime 2022年09月22日 14:16:00
 */
@Mapper(componentModel = "spring"
        ,nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface DataPanoramaPointConverter {

    /**
     * 转换 入口
     * @param addPointInDTO
     * @return
     */
    @Mappings({
            @Mapping(target = "pointId",expression = "java(String.valueOf(com.geoai.common.core.util.BizIdUtils.snowflakeId()))")
            ,@Mapping(target = "creatorId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
            ,@Mapping(target = "modifierId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
            ,@Mapping(target = "taskId",source = "taskId",defaultValue = "")
            ,@Mapping(target = "airPointId",source = "airPointId",defaultValue = "")
            ,@Mapping(target = "airLineId",source = "airLineId",defaultValue = "")
            ,@Mapping(target = "baseNestId",source = "baseNestId",defaultValue = "")
    })
    DataPanoramaPointEntity convert(DataPanoramaPointInDTO.AddPointInDTO addPointInDTO);

    DataPanoramaPointConverter INSTANCES = Mappers.getMapper(DataPanoramaPointConverter.class);

    /**
     * 转换 入口
     * @param updatePointInDTO
     * @return
     */
    @Mappings({
    })
    DataPanoramaPointEntity convert(DataPanoramaPointInDTO.UpdatePointInDTO updatePointInDTO);


    /**
     * 转换 出口
     * @param dataPanoramaPointEntity
     * @return
     */
    @Mappings({
    })
    DataPanoramaPointOutDTO.QueryPageOutDTO convertPage(DataPanoramaPointEntity dataPanoramaPointEntity);

    /**
     * 转换 出口
     * @param dataPanoramaPointEntity
     * @return
     */
    @Mappings({
    })
    DataPanoramaPointOutDTO.QueryLessOutDTO convertLess(DataPanoramaPointEntity dataPanoramaPointEntity);

    /**
     * 转换 出口
     * @param dataPanoramaPointEntity
     * @return
     */
    @Mappings({
    })
    DataPanoramaPointOutDTO.QueryOutDTO convert(DataPanoramaPointEntity dataPanoramaPointEntity);

}
