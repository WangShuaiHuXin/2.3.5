package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.BaseUavNestRefInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseUavNestRefOutDO;

import java.util.List;

/**
 * 基站无人机关系
 *
 * @author boluo
 * @date 2022-08-25
 */
public interface BaseUavNestRefManager {

    /**
     * 查询基站对应的无人机信息
     *
     * @param nestId 巢id
     * @return {@link List}<{@link BaseUavNestRefOutDO.EntityOutDO}>
     */
    List<BaseUavNestRefOutDO.EntityOutDO> selectListByNestId(String nestId);

    /**
     * 插入
     *
     * @param entityInDO 实体
     * @return int
     */
    int insert(BaseUavNestRefInDO.EntityInDO entityInDO);

    /**
     * 删除
     *
     * @param nestId    巢id
     * @param accountId 帐户id
     * @return int
     */
    int deleteByNestId(String nestId, String accountId);
}
