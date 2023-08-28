package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterDetailEntity;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisBaseSaveInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisDetailSaveInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailPageOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 账号信息转换器
 * @author Vastfy
 * @date 2022/4/25 16:09
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DataAnalysisDetailConverter {

    DataAnalysisDetailConverter INSTANCES = Mappers.getMapper(DataAnalysisDetailConverter.class);

    /**
     *
     * @param in
     * @return
     */
    @Mappings({
            @Mapping(source = "baseNestId",target = "nestId"),
            @Mapping(source = "orgCode",target = "orgId")
    })
    DataAnalysisDetailPageOutDTO convert(DataAnalysisCenterDetailEntity in);

    /**
     *
     * @param in
     * @return
     */
    @Mappings({
            @Mapping(source = "baseNestId",target = "nestId", defaultValue = "0L")
    })
    DataAnalysisDetailOutDTO convertDetail(DataAnalysisCenterDetailEntity in);

    /**
     *
     * @param in
     * @return
     */
    @Mappings({
            @Mapping(target = "centerDetailId", expression = "java(com.geoai.common.core.util.BizIdUtils.snowflakeId())")
            ,@Mapping(target = "creatorId", expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
            ,@Mapping(target = "modifierId", expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
            ,@Mapping(source = "srcDataType", target = "srcDataType",defaultValue = "1")
            ,@Mapping(target = "picType", expression = "java((in.getPicType()!=1 && in.getPicType()!=0)?0:in.getPicType())")
            ,@Mapping(target = "thumImageMarkPath",constant = "")
            ,@Mapping(target = "imageMarkPath",constant = "")
            ,@Mapping(source = "nestId",target = "baseNestId")
    })
    DataAnalysisCenterDetailEntity convert(DataAnalysisDetailSaveInDTO in);

    /**
     * 更新数据
     * @param in
     * @param dataAnalysisBaseSaveInDTO
     */
    void updateDetailDTO(@MappingTarget DataAnalysisDetailSaveInDTO in,  DataAnalysisBaseSaveInDTO dataAnalysisBaseSaveInDTO);
}
