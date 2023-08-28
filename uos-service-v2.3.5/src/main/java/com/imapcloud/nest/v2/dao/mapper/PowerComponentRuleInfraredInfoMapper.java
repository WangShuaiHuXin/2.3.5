package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.PowerComponentRuleInfraredInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 电力部件库红外测温规则信息表
 *
 * @author boluo
 * @date 2022-12-28
 */
@Mapper
public interface PowerComponentRuleInfraredInfoMapper extends BaseMapper<PowerComponentRuleInfraredInfoEntity> {

    /**
     * 批量插入
     *
     * @param entityList 实体列表
     * @return int
     */
    int batchInsert(@Param("entityList") List<PowerComponentRuleInfraredInfoEntity> entityList);

    /**
     * 批量更新
     *
     * @param entityList 实体列表
     * @return int
     */
    int batchUpdate(@Param("entityList") List<PowerComponentRuleInfraredInfoEntity> entityList);

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
