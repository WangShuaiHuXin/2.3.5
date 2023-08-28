package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.po.out.NhOrderRecordInfoOutPO;
import com.imapcloud.nest.v2.manager.dataobj.out.MissionOutDO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 架次
 *
 * @author boluo
 * @date 2022-11-01
 */
public interface MissionManager {

    /**
     * 查询架次对应的单位和基站信息
     *
     * @param missionIdCollection 任务id设置
     * @return {@link List}<{@link MissionOutDO.TaskMissionOutDO}>
     */
    List<MissionOutDO.TaskMissionOutDO> selectTaskMissionListByMissionIdList(Collection<Integer> missionIdCollection);

    /**
     * 查询架次信息
     *
     * @param missionIdCollection 任务id集合
     * @return {@link List}<{@link MissionOutDO}>
     */
    List<MissionOutDO> selectByMissionIdList(Collection<Integer> missionIdCollection);

    List<NhOrderRecordInfoOutPO> selectRecordInfo(Set<Integer> missionKey);
}
