package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.DataPanoramaDetailEntity;
import com.imapcloud.nest.v2.service.dto.in.DataPanoramaDetailInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaDetailOutDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaDetailConverter.java
 * @Description DataPanoramaDetailConverter
 * @createTime 2022年09月22日 14:16:00
 */
@Mapper(componentModel = "spring"
        ,nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
        ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface DataPanoramaDetailConverter {

    DataPanoramaDetailConverter INSTANCES = Mappers.getMapper(DataPanoramaDetailConverter.class);

    /**
     * 转换 入口
     * @param addDetailInDTO
     * @return
     */
    @Mappings({
            @Mapping(target = "detailId",expression = "java(String.valueOf(com.geoai.common.core.util.BizIdUtils.snowflakeId()))")
            ,@Mapping(target = "creatorId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
            ,@Mapping(target = "modifierId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
            ,@Mapping(target = "missionId",source = "missionId",defaultValue = "")
            ,@Mapping(target = "missionRecordsId",source = "missionRecordsId",defaultValue = "")
            ,@Mapping(target = "airLineId",source = "airLineId",defaultValue = "")
            ,@Mapping(target = "airPointId",source = "airPointId",defaultValue = "")
            ,@Mapping(target = "missionRecordTime",source = "missionRecordTime")
            ,@Mapping(target = "baseNestId",source = "baseNestId",defaultValue = "")
            ,@Mapping(target = "missionFlyIndex",source = "missionFlyIndex",defaultValue = "")
    })
    DataPanoramaDetailEntity convert(DataPanoramaDetailInDTO.AddDetailInDTO addDetailInDTO);


    /**
     * 转换 出口
     * @param dataPanoramaDetailEntity
     * @return
     */
    @Mappings({
    })
    DataPanoramaDetailOutDTO.QueryPageOutDTO convertPage(DataPanoramaDetailEntity dataPanoramaDetailEntity);

    /**
     * 转换 出口
     * @param dataPanoramaDetailEntity
     * @return
     */
    @Mappings({
    })
    DataPanoramaDetailOutDTO.QueryLessOutDTO convertLess(DataPanoramaDetailEntity dataPanoramaDetailEntity);

    /**
     * 转换 出口
     * @param dataPanoramaDetailEntity
     * @return
     */
    @Mappings({
    })
    DataPanoramaDetailOutDTO.QueryOutDTO convert(DataPanoramaDetailEntity dataPanoramaDetailEntity);

}
