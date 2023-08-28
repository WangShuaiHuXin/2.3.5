package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.DataAnalysisBasePageInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisBasePageOutDTO;
import com.imapcloud.nest.v2.web.vo.req.DataAnalysisTaskPageReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataAnalysisCenterBaseRespVO;
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
public interface DataAnalysisBaseTransformer {

    DataAnalysisBaseTransformer INSTANCES = Mappers.getMapper(DataAnalysisBaseTransformer.class);

    /**
     * 转换入口
     * @param reqVO
     * @return
     */
    DataAnalysisBasePageInDTO transform(DataAnalysisTaskPageReqVO reqVO);

    /**
     *  转换出口
     * @param reqVO
     * @return
     */
    @Mappings({
            @Mapping(target = "subType", expression = "java(reqVO.getSubType()==null?null:(reqVO.getSubType().booleanValue()?1:0))")
    })
    DataAnalysisCenterBaseRespVO transform(DataAnalysisBasePageOutDTO reqVO);

}
