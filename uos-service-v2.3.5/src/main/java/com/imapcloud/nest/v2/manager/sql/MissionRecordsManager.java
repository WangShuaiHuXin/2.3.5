package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.out.MissionRecordsOutDO;

import java.util.List;
import java.util.Set;

/**
 * 架次记录
 *
 * @author boluo
 * @date 2022-11-30
 */
public interface MissionRecordsManager {

    /**
     * 查询架次记录信息
     *
     * @param missionRecordsIdList 任务记录id列表
     * @return {@link List}<{@link MissionRecordsOutDO}>
     */
    List<MissionRecordsOutDO> selectByMissionRecordsIdList(List<Long> missionRecordsIdList);

    List<MissionRecordsOutDO> selectByMissionIdlist(Set<Integer> integers);
}
