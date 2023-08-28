package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.model.MissionVideoEntity;
import com.imapcloud.nest.v2.manager.dataobj.in.MissionVideoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.MissionVideoOutDO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 视频
 *
 * @author boluo
 * @date 2022-11-01
 */
public interface MissionVideoManager {

    /**
     * 查询满足tagVersion的数据
     *
     * @param tagVersion 标记版本
     * @return {@link List}<{@link MissionVideoOutDO}>
     */
    List<MissionVideoOutDO> queryListByTagVersion(int tagVersion);

    /**
     * 更新标签版本通过id列表
     *
     * @param tagVersion 标记版本
     * @param idList     id列表
     * @return int
     */
    int updateTagVersionByIdList(int tagVersion, List<Long> idList);

    /**
     * 查询删除的数据
     *
     * @param tagVersion 标记版本
     * @return {@link List}<{@link MissionVideoOutDO}>
     */
    List<MissionVideoOutDO> queryDeleteList(int tagVersion);

    /**
     * 更新物理删除通过id
     *
     * @param idList id列表
     * @return int
     */
    int updatePhysicsDeleteByIdList(Collection<Long> idList);

    /**
     * 更新视频
     *
     * @param missionVideoInDOList 任务视频dolist
     * @return int
     */
    int updateVideoUrl(List<MissionVideoInDO> missionVideoInDOList);

    List<MissionVideoEntity> getAllVideoByRecordId(Integer recordId, String orgCode);

    int selectNum(Integer videoId, String orgCode);
}
