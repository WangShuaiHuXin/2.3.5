package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultGroupPageInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisTraceSpacetimeInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisResultGroupOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisTraceSpacetimeOutDTO;
import com.imapcloud.nest.v2.web.vo.req.DataAnalysisResultGroupPageReqVO;
import com.imapcloud.nest.v2.web.vo.req.DataAnalysisTraceSpacetimeReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataAnalysisResultGroupResqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataAnalysisTraceSpacetimeRespVO;
import org.mapstruct.Mapper;

/**
 * @Classname DataAnalysisResultGroupTransformer
 * @Description 数据分析问题统计结果转化类
 * @Date 2022/10/11 16:18
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface DataAnalysisResultGroupTransformer {

    DataAnalysisResultGroupPageInDTO transform(DataAnalysisResultGroupPageReqVO vo);

    DataAnalysisResultGroupResqVO.ResultGroupRespVO transform(DataAnalysisResultGroupOutDTO.ResultGroupOutDTO dto);

    DataAnalysisResultGroupResqVO.ResultGroupExportRespVO transform(DataAnalysisResultGroupOutDTO.ResultGroupExportOutDTO dto);

    DataAnalysisTraceSpacetimeInDTO transformTraceSpacetime(DataAnalysisTraceSpacetimeReqVO vo);

    DataAnalysisTraceSpacetimeRespVO transformTraceSpacetime(DataAnalysisTraceSpacetimeOutDTO dto);
}
