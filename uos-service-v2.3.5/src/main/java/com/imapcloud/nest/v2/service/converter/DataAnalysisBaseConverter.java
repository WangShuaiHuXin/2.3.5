package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterBaseEntity;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisAggSaveInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisBaseSaveInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisBasePageOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 账号信息转换器
 * @author Vastfy
 * @date 2022/4/25 16:09
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DataAnalysisBaseConverter {

    DataAnalysisBaseConverter INSTANCES = Mappers.getMapper(DataAnalysisBaseConverter.class);

    /**
     * 转换 入口
     * @param in
     * @return
     */
    @Mappings({
            @Mapping(source = "baseNestId",target = "nestId", defaultValue = "0L")
    })
    DataAnalysisBasePageOutDTO convert(DataAnalysisCenterBaseEntity in);

    /**
     * 转换入口
     * @param in
     * @return
     */
    @Mappings({
            @Mapping(target = "centerBaseId",expression = "java(com.geoai.common.core.util.BizIdUtils.snowflakeId())")
            ,@Mapping(target = "creatorId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
            ,@Mapping(target = "modifierId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
            ,@Mapping(source = "baseName",target = "baseName",defaultValue = "")
            ,@Mapping(source = "nestName",target = "nestName",defaultValue = "")
            ,@Mapping(source = "nestId",target = "baseNestId")
    })
    DataAnalysisCenterBaseEntity convert(DataAnalysisBaseSaveInDTO in);

    /**
     * 转换入口
     * @param in
     * @return
     */
    DataAnalysisBaseSaveInDTO convert(DataAnalysisAggSaveInDTO in);

}
