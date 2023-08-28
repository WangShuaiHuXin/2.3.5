package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.PowerComponentRuleInfraredInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerComponentRuleInfraredInfoOutDO;

import java.util.Collection;
import java.util.List;

/**
 * 电力部件库红外测温规则信息表
 *
 * @author boluo
 * @date 2022-12-28
 */
public interface PowerComponentRuleInfraredInfoManager {

    /**
     * 批量插入
     *
     * @param componentRuleInfraredInfoInDOCollection PowerComponentRuleInfraredInfoInDO
     * @return int
     */
    int batchInsert(Collection<PowerComponentRuleInfraredInfoInDO> componentRuleInfraredInfoInDOCollection);

    /**
     * 选择列表组件id集合
     *
     * @param componentIdCollection 组件id集合
     * @return {@link List}<{@link PowerComponentRuleInfraredInfoOutDO}>
     */
    List<PowerComponentRuleInfraredInfoOutDO> selectListByComponentIdCollection(Collection<String> componentIdCollection);

    /**
     * 批量更新
     *
     * @param updateInfoList 更新信息列表
     * @return int
     */
    int batchUpdate(List<PowerComponentRuleInfraredInfoInDO> updateInfoList);

    /**
     * 逻辑删除
     *
     * @param componentRuleIdList 删除表
     * @param accountId  帐户id
     * @return int
     */
    int deleteByComponentRuleIdList(List<String> componentRuleIdList, String accountId);

    /**
     * 按组件id删除
     *
     * @param componentId 组件id
     * @param accountId   帐户id
     * @return int
     */
    int deleteByComponentId(String componentId, String accountId);
}
