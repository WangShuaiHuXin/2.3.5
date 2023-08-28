package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.MissionVideoPhotoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 视屏抽帧的图片 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2021-06-30
 */
public interface MissionVideoPhotoMapper extends BaseMapper<MissionVideoPhotoEntity> {

    /**
     * 查询删除
     *
     * @param tagVersion 标记版本
     * @return {@link List}<{@link MissionVideoPhotoEntity}>
     */
    List<MissionVideoPhotoEntity> queryDeleteList(@Param("tagVersion") int tagVersion);

    /**
     * 更新物理删除
     *
     * @param idList id列表
     * @return int
     */
    int updatePhysicsDelete(@Param("idList") Collection<Long> idList);

    int deleteByRecordId(@Param("recordIdList") List<Integer> missionRecordIds);
}
