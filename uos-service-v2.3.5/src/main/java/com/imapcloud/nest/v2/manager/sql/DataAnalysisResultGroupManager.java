package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.DataAnalysisResultGroupInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataAnalysisResultGroupOutDO;

import java.util.List;

/**
 * 问题组
 *
 * @author boluo
 * @date 2022-10-11
 */
public interface DataAnalysisResultGroupManager {

    /**
     * 插入
     *
     * @param dataAnalysisResultGroupInDO 做数据分析结果
     * @return int
     */
    int insert(DataAnalysisResultGroupInDO dataAnalysisResultGroupInDO);

    /**
     * 查询问题组信息
     *
     * @param resultGroupId 结果组id
     * @return {@link DataAnalysisResultGroupOutDO}
     */
    DataAnalysisResultGroupOutDO selectByResultGroupId(String resultGroupId);

    /**
     * 逻辑删除问题组
     *
     * @param resultGroupIdList 结果组id列表
     * @return int
     */
    int deleteByResultGroupIdList(List<String> resultGroupIdList);

    /**
     * 更新问题组最早和最新时间
     *
     * @param dataAnalysisResultGroupInDOList dolist数据分析结果
     * @return int
     */
    int updateTime(List<DataAnalysisResultGroupInDO> dataAnalysisResultGroupInDOList);
}
