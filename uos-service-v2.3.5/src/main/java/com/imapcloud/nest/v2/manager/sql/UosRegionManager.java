package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.out.UosRegionOutDO;

import java.util.List;

/**
 * 区域
 *
 * @author boluo
 * @date 2022-08-26
 */
public interface UosRegionManager {

    /**
     * 查询区域信息
     *
     * @param regionId 区域id
     * @return <{@link UosRegionOutDO}>
     */
    UosRegionOutDO selectOneByRegionId(String regionId);

    /**
     * 查询区域列表
     *
     * @param regionIdList 区域id列表
     * @return {@link List}<{@link UosRegionOutDO}>
     */
    List<UosRegionOutDO> selectByRegionIdList(List<String> regionIdList);
}
