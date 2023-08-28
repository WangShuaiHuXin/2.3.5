package com.imapcloud.nest.v2.manager.sql;

import java.util.List;

public interface DataAnalysisMarkManager {

    /**
     * 更新标记的 add及addrImg为空
     * @param markIds
     */
    void updateMarkIdByIds(List<Long> markIds);

    /**
     * 更新分组内标记的addrImG为空
     * @param groupIdList
     */
    void updateMarkIdByGroupIds(List<String> groupIdList);
}
