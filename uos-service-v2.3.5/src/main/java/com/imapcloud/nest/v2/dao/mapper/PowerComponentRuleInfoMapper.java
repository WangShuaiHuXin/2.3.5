package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.PowerComponentRuleInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 电力部件库规则信息表
 *
 * @author boluo
 * @date 2022-11-24
 */
@Mapper
public interface PowerComponentRuleInfoMapper extends BaseMapper<PowerComponentRuleInfoEntity> {

    /**
     * 批量插入
     *
     * @param powerComponentRuleInfoEntityList 电源组件实体规则信息列表
     * @return int
     */
    int batchInsert(@Param("entityList") List<PowerComponentRuleInfoEntity> powerComponentRuleInfoEntityList);

    /**
     * 批量更新
     *
     * @param powerComponentRuleInfoEntityList 电源组件实体规则信息列表
     * @return int
     */
    int batchUpdate(@Param("entityList") List<PowerComponentRuleInfoEntity> powerComponentRuleInfoEntityList);

    /**
     * 按组件删除规则id列表
     *
     * @param componentRuleIdList 组件规则id列表
     * @param accountId           帐户id
     * @return int
     */
    int deleteByComponentRuleIdList(@Param("componentRuleIdList") List<String> componentRuleIdList, @Param("accountId") String accountId);

    /**
     * 按组件id删除
     *
     * @param componentId 组件id
     * @param accountId   帐户id
     * @return int
     */
    int deleteByComponentId(@Param("componentId") String componentId, @Param("accountId") String accountId);
}
