package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.NestRtkInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.NestRtkOutDO;

/**
 * RTK
 *
 * @author boluo
 * @date 2022-08-25
 */
public interface NestRtkManager {

    /**
     * 插入
     *
     * @param nestRtkEntityInDO rtk实体
     * @return int
     */
    int insert(NestRtkInDO.NestRtkEntityInDO nestRtkEntityInDO);

    /**
     * 查询基站的RTK信息
     *
     * @param nestId 巢id
     * @return {@link NestRtkOutDO.NestRtkEntityOutDO}
     */
    NestRtkOutDO.NestRtkEntityOutDO selectByNestId(String nestId);

    /**
     * 更新RTK
     *
     * @param nestRtkEntityInDO rtk实体
     * @return int
     */
    int updateByNestId(NestRtkInDO.NestRtkEntityInDO nestRtkEntityInDO);

    /**
     * 删除
     *
     * @param nestId 巢id
     * @return int
     */
    int deleteByNestId(String nestId);
}
