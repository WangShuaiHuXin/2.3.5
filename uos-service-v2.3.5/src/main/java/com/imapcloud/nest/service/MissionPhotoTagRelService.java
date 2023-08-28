package com.imapcloud.nest.service;

import com.imapcloud.nest.model.MissionPhotoTagRelEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 照片和标签关系表 服务类
 * </p>
 *
 * @author zheng
 * @since 2021-03-24
 */
public interface MissionPhotoTagRelService extends IService<MissionPhotoTagRelEntity> {

    List<MissionPhotoTagRelEntity> getMissionPhotoTagList();

    void deleteByTagIdAndPhotoIdList(@Param("tagId") Integer tagId, @Param("photoIdList") List<Integer> photoIdList);
}
