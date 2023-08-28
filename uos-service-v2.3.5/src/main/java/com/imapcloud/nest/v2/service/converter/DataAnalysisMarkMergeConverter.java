package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkMergeEntity;
import com.imapcloud.nest.v2.web.vo.req.DataAnalysisResultGroupReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DataAnalysisMarkMergeConverter {

    DataAnalysisMarkMergeConverter INSTANCES = Mappers.getMapper(DataAnalysisMarkMergeConverter.class);

    @Mappings(
            {
                    @Mapping(target = "resultGroupId", source = "groupId"),
                    @Mapping(target = "creatorId", expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())"),
                    @Mapping(target = "modifierId", expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())"),
            }
    )
    DataAnalysisMarkMergeEntity convert(DataAnalysisResultGroupReqVO.ResultMergeReqVO vo);
}
