package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.out.DataAnalysisAlgoProblemTypeRefOutDO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisAlgoProblemTypeRefOutDTO;

import java.util.Collection;
import java.util.List;

public interface DataAnalysisAlgoProblemTypeRefManager {
    List<DataAnalysisAlgoProblemTypeRefOutDTO> getDataAnalysisAlgoProblemTypeRefOutDTO(
            List<String> dataAnalysisTopicProblemPageList);

    /**
     * 选择列表类型关系id列表
     *
     * @param typeRelationIdCollection 类型关系id集合
     * @return {@link List}<{@link DataAnalysisAlgoProblemTypeRefOutDO}>
     */
    List<DataAnalysisAlgoProblemTypeRefOutDO> selectListByTypeRelationIdList(Collection<String> typeRelationIdCollection);
}
