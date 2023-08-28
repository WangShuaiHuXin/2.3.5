package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterDetailEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultGroupEntity;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisTraceSpacetimeOutPO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultImageInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisResultGroupOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisTraceSpacetimeOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


/**


/**
 * @Classname DataAnalysisResultGroupConverter
 * @Description 数据分析问题统计结果分组转化器
 * @Date 2022/10/11 17:19
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface DataAnalysisResultGroupConverter {

    DataAnalysisResultGroupOutDTO.ResultGroupOutDTO convertSpacetime(DataAnalysisResultGroupEntity entity);

    DataAnalysisTraceSpacetimeOutDTO convertSpacetime(DataAnalysisCenterDetailEntity entity);

    DataAnalysisResultGroupOutDTO.ResultGroupExportOutDTO convertExport(DataAnalysisResultGroupEntity entity);
    DataAnalysisResultGroupConverter INSTANCES = Mappers.getMapper(DataAnalysisResultGroupConverter.class);

    /**
     * 将result→resultGroup
     *
     * @param dataAnalysisResultEntity
     * @return
     */
    @Mappings({
            @Mapping(target = "earliestTime", expression = "java(dataAnalysisResultEntity.getPhotoCreateTime())")
            , @Mapping(target = "latestTime", expression = "java(dataAnalysisResultEntity.getPhotoCreateTime())")
    })
    DataAnalysisResultGroupEntity convertToEntity(DataAnalysisResultEntity dataAnalysisResultEntity);

    DataAnalysisResultImageInDTO convertResultImage(DataAnalysisTraceSpacetimeOutPO po);


}
