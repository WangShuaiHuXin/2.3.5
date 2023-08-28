package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.MissionPhotoTagRelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 照片和标签关系表 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2021-03-24
 */
public interface MissionPhotoTagRelMapper extends BaseMapper<MissionPhotoTagRelEntity> {

    @Select("SELECT mpt.* FROM `mission_photo_tag_rel` mpt,mission_photo mp where mpt.mission_photo_id = mp.id and mpt.deleted = 0 and mp.deleted = 0")
    List<MissionPhotoTagRelEntity> getMissionPhotoTagList();

    void deleteByTagIdAndPhotoIdList(@Param("tagId") Integer tagId, @Param("photoIdList") List<Integer> photoIdList);
}
