package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.BaseNestOrgRefInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOrgRefOutDO;

import java.util.List;

/**
 * 基站单位关系
 *
 * @author boluo
 * @date 2022-08-25
 */
public interface BaseNestOrgRefManager {

    /**
     * 插入
     *
     * @param baseNestOrgRefEntityInDOList 基地巢org ref实体
     * @return int
     */
    int batchInsert(List<BaseNestOrgRefInDO.BaseNestOrgRefEntityInDO> baseNestOrgRefEntityInDOList);

    /**
     * 通过基站ID逻辑删除
     *
     * @param nestId    巢id
     * @param accountId 帐户id
     * @return int
     */
    int deleteByNestId(String nestId, String accountId);

    /**
     * 查询基站的单位
     *
     * @param nestId 巢id
     * @return {@link BaseNestOrgRefOutDO}
     */
    List<BaseNestOrgRefOutDO> selectOneByNestId(String nestId);

    /**
     * 查询基站单位关系
     *
     * @param nestIdList 巢id
     * @return {@link List}<{@link BaseNestOrgRefOutDO}>
     */
    List<BaseNestOrgRefOutDO> selectByNestIdList(List<String> nestIdList);

    /**
     * 根据基站ID和单位ID删除
     *
     * @param nestId      巢id
     * @param orgCodeList 组织代码列表
     * @param accountId   帐户id
     * @return int
     */
    int deleteByNestIdAndOrgCodeList(String nestId, List<String> orgCodeList, String accountId);
}
