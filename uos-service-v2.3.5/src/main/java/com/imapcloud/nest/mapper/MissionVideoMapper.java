package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.MissionVideoEntity;
import com.imapcloud.nest.v2.manager.dataobj.FileUrlMappingDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-08-14
 */
public interface MissionVideoMapper extends BaseMapper<MissionVideoEntity> {
    /**
     * 查找该架次对应的录屏视频
     *
     * @param recordId
     * @return
     */
    @Select("SELECT id FROM mission_video WHERE mission_records_id = #{recordId} AND type = 1 AND deleted = 0")
    Long getIdByRecordId(Integer recordId);

    /**
     * 查找该架次对应的高清视频
     *
     * @param recordId
     * @return
     */
    @Select("SELECT * FROM mission_video WHERE mission_records_id = #{recordId} AND type = 2 AND deleted = 0 limit 1")
    MissionVideoEntity getVideoByRecordsId(Integer recordId);

    IPage<MissionVideoEntity> getVideoByRecordId(@Param("page") IPage<MissionPhotoEntity> page,@Param("recordId")Integer recordId,@Param("orgCode")String orgCode);

    void deleteByRecordId(@Param("recordIdList") List<Integer> recordIdList);

    /**
     * 通过架次执行id获取全部视频的名称
     * @return
     */
    List<String> getVideoNameByRecordId(@Param("recordId") Integer recordId, @Param("types") List<Integer> types);

    /**
     * 通过架次执行id获取全部视频的名称跟URL
     * @return
     */
    List<Map> getVideoNameAndUrlByRecordId(@Param("recordId") Integer recordId, @Param("types") List<Integer> types);

    /**
     * 通过idList获取视频的名称
     *
     * @param idList
     * @return
     */
    List<String> getNameByIdList(@Param("idList") List idList, @Param("types") List<Integer> types);

    /**
     * 通过idList获取视频的名称跟URL
     *
     * @param idList
     * @return
     */
    List<Map> getNameAndUrlByIdList(@Param("idList") List idList, @Param("types") List<Integer> types);

    /**
     * 通过recordId获取fileIdList
     */
    @Select("SELECT file_id AS fileId FROM mission_video WHERE mission_records_id = #{recordId} AND deleted = 0 AND type != 1")
    List<String> getFileIdListByRecordId(Integer recordId);

    /**
     * 通过视频id获取是否已结束录屏
     *
     * @param videoId
     * @return
     */
    @Select("SELECT record_status FROM mission_video WHERE id = #{videoId} ")
    Integer getRecordStatusByVideoId(Long videoId);

    /**
     * 获取下载到服务器的视频（非录屏）
     *
     * @param recordId
     * @return
     */
    @Select("SELECT COUNT(id) FROM mission_video WHERE type = 1 AND mission_records_id = #{recordId} AND deleted = 0")
    Integer getVideoNum(Integer recordId);

    List<MissionRecordsEntity> getMissionRecords(@Param("startTime") String startTime, @Param("endTime") String endTime);

    List<Map> getInspect(@Param("missionRecordsIds") List<Integer> missionRecordsIds);

    List<FileUrlMappingDO> getVideoUrlByIdList(@Param("idList") List<Integer> idList);

    @Select("SELECT type FROM mission_video WHERE id = #{id} ")
    Integer getVideoType(Integer id);

    @Select("select count(id) from mission_video where mission_records_id = #{recordId} and deleted = 0")
    Integer countByRecordId(Integer recordId);

    /**
     * 查询删除的数据
     *
     * @param tagVersion 标记版本
     * @return {@link List}<{@link MissionVideoEntity}>
     */
    List<MissionVideoEntity> queryDeleteList(@Param("tagVersion") int tagVersion);

    /**
     * 更新物理删除
     *
     * @param idList id列表
     * @return int
     */
    int updatePhysicsDelete(@Param("idList") Collection<Long> idList);

    /**
     * 更新视频
     *
     * @param missionVideoEntityList 任务视频实体列表
     * @return int
     */
    int updateVideoUrl(@Param("entityList") List<MissionVideoEntity> missionVideoEntityList);

    List<MissionVideoEntity> getAllVideoByRecordId(@Param("recordId") Integer recordId,@RequestParam("orgCode") String orgCode);
}
