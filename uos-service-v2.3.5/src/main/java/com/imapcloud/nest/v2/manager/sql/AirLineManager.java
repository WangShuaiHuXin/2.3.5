package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.out.AirLineOutDO;

import java.util.List;

/**
 * 航线
 *
 * @author boluo
 * @date 2022-11-30
 */
public interface AirLineManager {

    /**
     * 选择行id列表
     *
     * @param airLineIdList 空气行id列表
     * @return {@link List}<{@link AirLineOutDO}>
     */
    List<AirLineOutDO> selectByAirLineIdList(List<Integer> airLineIdList);
}
