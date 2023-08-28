package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.v2.dao.po.in.NhOrderPhotoInPO;
import com.imapcloud.nest.v2.dao.po.out.NhOrderPhotoOutPO;
import com.imapcloud.nest.v2.manager.dataobj.out.MissionPhotoOutDO;

import java.util.Collection;
import java.util.List;

/**
 * 架次图片信息
 *
 * @author boluo
 * @date 2022-10-26
 */
public interface MissionPhotoManager {

    /**
     * 查询不等于tagVersion的记录
     *
     * @param tagVersion 标记版本
     * @return {@link List}<{@link MissionPhotoOutDO}>
     */
    List<MissionPhotoOutDO> queryListByTagVersion(int tagVersion);

    /**
     * 批量更新tag版本
     *
     * @param tagVersion    标记版本
     * @param successIdList 成功id列表
     * @return int
     */
    int updateTagVersionByIdList(int tagVersion, List<Long> successIdList);

    /**
     * 删除
     *
     * @param tagVersion 标记版本
     * @return {@link List}<{@link MissionPhotoOutDO}>
     */
    List<MissionPhotoOutDO> queryDeleteList(int tagVersion);

    /**
     * 更新物理删除状态通过id列表
     *
     * @param idList 收集
     * @return int
     */
    int updatePhysicsDeleteByIdList(Collection<Long> idList);

    /**
     * 查询照片num
     *
     * @param missionRecordIdList 任务id列表
     * @return {@link List}<{@link MissionPhotoOutDO.PhotoNumOutDO}>
     */
    List<MissionPhotoOutDO.PhotoNumOutDO> queryPhotoNum(List<Integer> missionRecordIdList);

    /**
     * 更新删除
     *
     * @param idList id列表
     * @return int
     */
    int updateDelete(List<Long> idList);

    /**
     * 选择通过照片id列表
     *
     * @param photoIdList 照片id列表
     * @return {@link List}<{@link MissionPhotoOutDO}>
     */
    List<MissionPhotoOutDO> selectByPhotoIdList(Collection<Integer> photoIdList);

    /**
     * 选择任务记录id
     *
     * @param missionRecordId 任务记录id
     * @return {@link List}<{@link MissionPhotoOutDO}>
     */
    List<MissionPhotoOutDO> selectByMissionRecordId(Integer missionRecordId);

    NhOrderPhotoOutPO selectPageByCondition(NhOrderPhotoInPO inPO);
}
