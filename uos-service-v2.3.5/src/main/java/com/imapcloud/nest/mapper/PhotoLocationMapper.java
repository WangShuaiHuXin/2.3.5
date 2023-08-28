package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.PhotoLocationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhengxd
 * @since 2021-06-02
 */
public interface PhotoLocationMapper extends BaseMapper<PhotoLocationEntity> {

    /**
     * 根据照片idList获取照片位置信息
     * @param page
     * @param photoIdList
     * @return
     */
    IPage<PhotoLocationEntity> getPhotoLocationList(IPage<PhotoLocationEntity> page, @Param("photoIdList") List photoIdList);

    List<MissionRecordsEntity> getMission(@Param("missionRecordsId") Integer missionRecordsId, @Param("taskId") Integer taskId,@Param("flag")Integer flag);

    List<MissionPhotoEntity> getMissionPhoto(Integer missionRecordsId);
}
