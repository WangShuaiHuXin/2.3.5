package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.DataCenterEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionVideoEntity;
import com.imapcloud.nest.pojo.dto.MissionRecordTaskInfoDTO;
import com.imapcloud.nest.pojo.dto.MissionRecordsDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据中心 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2021-06-01
 */
public interface DataCenterMapper extends BaseMapper<DataCenterEntity> {
    List<Map> getOrthoInspect(@Param("missionRecordsIds") List<Integer> missionRecordsIds);
    List<Map> getPointCloudInspect(@Param("missionRecordsIds") List<Integer> missionRecordsIds);
    List<Map> getTiltInspect(@Param("missionRecordsIds") List<Integer> missionRecordsIds);
    List<Map> getVectorInspect(@Param("missionRecordsIds") List<Integer> missionRecordsIds);
    List<Map> getPanoramaInspect(@Param("missionRecordsIds") List<Integer> missionRecordsIds);
    List<Map> getMultispectralInspect(@Param("missionRecordsIds") List<Integer> missionRecordsIds);

    List<MissionRecordsDto> getTaskMissions(@Param("tagId") Integer tagId, @Param("dataType") Integer dataType, @Param("name") String name, @Param("orgCode")String orgCode);

    /**
     * 根据架次ids获取任务的信息
     * @param missionRecordsIds
     * @return
     */
    List<MissionRecordTaskInfoDTO> getTaskInfoByMissionRecordId(@Param("missionRecordsIds") List<Integer> missionRecordsIds);

    @Select("select t.id from sys_task_tag stt,task t where stt.task_id = t.id and stt.deleted = 0 and t.deleted = 0 AND stt.tag_id = #{tagId}")
    List<Integer> getTaskId(Integer tagId);

    @Select("select id from mission_photo where deleted = 1 and photo_url not in (select photo_url from mission_photo where deleted = 0 group by photo_url having count(photo_url) > 1)")
    List<Long> getPhotoNoRePeat();

    @Select("select * from mission_video where deleted = 1 and video_url not in (select video_url from mission_video where deleted = 0 group by video_url having count(video_url) > 1)")
    List<MissionVideoEntity> getVideoNoRePeat();

    List<Integer> getPanoramaNoRePeatUrl(@Param("ids") List<Long> ids);
    List<Integer> getVectorNoRePeatUrl(@Param("ids") List<Long> vectorIds);

    List<Integer> getOrthoNoRePeatUrl(@Param("ids") List<Long> vectorIds);
    List<Integer> getTiltNoRePeatUrl(@Param("ids") List<Long> vectorIds);
    List<Integer> getPointCloudNoRePeatUrl(@Param("ids") List<Long> vectorIds);
    List<Integer> getPollutionGridNoRePeatUrl(@Param("ids") List<Long> vectorIds);
    List<Integer> getMultispectralNoRePeatUrl(@Param("ids") List<Long> multispectralIdList);

    List<Integer> getOrthoFileInfoIds(@Param("recordsIds") List<Integer> recordsIds);
    List<Integer> getVectorFileInfoIds(@Param("recordsIds") List<Integer> recordsIds);
    List<Integer> getTiltFileInfoIds(@Param("recordsIds") List<Integer> recordsIds);
    List<Integer> getPointCloudFileInfoIds(@Param("recordsIds") List<Integer> recordsIds);
    List<Integer> getPanoramaFileInfoIds(@Param("recordsIds") List<Integer> recordsIds);

    List<Integer> getRecords(@Param("startTime")String startTime, @Param("endTime")String endTime);

    List<String> getMissionPhotos(@Param("recordsIds") List<Integer> recordsIds);

    List<String> getMissionVideos(@Param("recordsIds") List<Integer> recordsIds);

    @Update("update mission_video set physical_deleted = 1 where id = #{id}")
    void updatePhysical(Long id);
}
