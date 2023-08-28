package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.PowerComponentRuleInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerComponentRuleInfoOutDO;

import java.util.List;

/**
 * 电力部件库规则信息表
 *
 * @author boluo
 * @date 2022-11-24
 */
public interface PowerComponentRuleInfoManager {

    /**
     * 查询部件库规则
     *
     * @param componentId 组件id
     * @return {@link List}<{@link PowerComponentRuleInfoOutDO}>
     */
    List<PowerComponentRuleInfoOutDO> selectByComponentId(String componentId);

    /**
     * 批量插入
     *
     * @param powerComponentRuleInfoInDOList inDo
     * @return int
     */
    int batchInsert(List<PowerComponentRuleInfoInDO> powerComponentRuleInfoInDOList);

    /**
     * 批量更新
     *
     * @param powerComponentRuleInfoInDOList inDo
     * @return int
     */
    int batchUpdate(List<PowerComponentRuleInfoInDO> powerComponentRuleInfoInDOList);

    /**
     * 逻辑删除
     *
     * @param componentRuleIdList 组件规则id列表
     * @param accountId           组件规则id列表
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

    /**
     * 选择通过组件id列表
     *
     * @param componentIdList 组件id列表
     * @return {@link List}<{@link PowerComponentRuleInfoOutDO}>
     */
    List<PowerComponentRuleInfoOutDO>  selectByComponentIdList(List<String> componentIdList);
}
