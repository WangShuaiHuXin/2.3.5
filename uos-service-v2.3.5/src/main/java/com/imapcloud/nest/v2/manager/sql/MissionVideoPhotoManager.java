package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.out.MissionVideoPhotoOutDO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 视频照片
 *
 * @author boluo
 * @date 2022-11-01
 */
public interface MissionVideoPhotoManager {


    /**
     * 查询满足版本数据
     *
     * @param tagVersion 标记版本
     * @return {@link List}<{@link MissionVideoPhotoOutDO}>
     */
    List<MissionVideoPhotoOutDO> queryListByTagVersion(int tagVersion);

    /**
     * 更新标签版本通过id列表
     *
     * @param tagVersion 标记版本
     * @param idList    idd列表
     * @return int
     */
    int updateTagVersionByIdList(int tagVersion, Collection<Long> idList);

    /**
     * 查询删除的数据
     *
     * @param tagVersion 标记版本
     * @return {@link List}<{@link MissionVideoPhotoOutDO}>
     */
    List<MissionVideoPhotoOutDO> queryDeleteList(int tagVersion);

    /**
     * 更新物理删除通过id
     *
     * @param idList id列表
     * @return int
     */
    int updatePhysicsDeleteByIdList(Collection<Long> idList);

    /**
     * 更新删除
     *
     * @param idList id列表
     * @return int
     */
    int updateDelete(List<Integer> idList);

    int selectNum(String[] ids, String orgCode);

}
