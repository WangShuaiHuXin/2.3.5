package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.BaseNestOrgRefEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 基站和单位关系
 *
 * @author boluo
 * @date 2022-08-23
 */
@Mapper
public interface BaseNestOrgRefMapper extends BaseMapper<BaseNestOrgRefEntity> {

    /**
     * 通过基站ID逻辑删除
     *
     * @param nestId 基站ID
     * @param accountId 操作人ID
     * @return int
     */
    int deleteByNestId(@Param("nestId") String nestId, @Param("accountId") String accountId);

    /**
     * 批量插入
     *
     * @param baseNestOrgRefEntityList 实体
     * @return int
     */
    int batchInsert(@Param("entityList")List<BaseNestOrgRefEntity> baseNestOrgRefEntityList);

    /**
     * 根据基站ID和单位ID删除
     *
     * @param nestId      巢id
     * @param orgCodeList 组织代码列表
     * @param accountId   帐户id
     * @return int
     */
    int deleteByNestIdAndOrgCodeList(@Param("nestId") String nestId, @Param("orgCodeList") List<String> orgCodeList, @Param("accountId") String accountId);
}
