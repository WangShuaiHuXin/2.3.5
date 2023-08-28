package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultGroupPageInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisTraceSpacetimeInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisResultGroupOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisTraceSpacetimeOutDTO;

import java.util.List;
import java.util.Map;

/**
 * 问题组业务
 *
 * @author boluo
 * @date 2022-10-11
 */
public interface DataAnalysisResultGroupService {

    /**
     * 据分析问题统计结果分组列表分页查询
     */
    PageResultInfo<DataAnalysisResultGroupOutDTO.ResultGroupOutDTO> pageResultGroupList(DataAnalysisResultGroupPageInDTO dto);

    /**
     * 全量问题统计结果列表分页查询
     */
    List<DataAnalysisResultGroupOutDTO.ResultGroupOutDTO> listResultGroupList(DataAnalysisResultGroupPageInDTO dto);

    /**
     * 删除分析问题统计结果分组
     */
    Boolean deleteResultGroup(List<String> problemResultList);

    /**
     * 导出问题列表
     */
    DataAnalysisResultGroupOutDTO.ResultGroupExportResultOutDTO resultGroupExportList(List<String> problemResultList);

    /**
     * 时空追溯
     */
    Map<String, List<DataAnalysisTraceSpacetimeOutDTO>> traceSpacetime(DataAnalysisTraceSpacetimeInDTO inDTO);

    int selectNum(List<String> list, String orgCode);
}
