package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.PhotoTagSourceDTO;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.StationDefectPhotoDTO;
import com.imapcloud.nest.v2.service.dto.out.MissionPhotoPointOutDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface MissionPhotoMapper extends BaseMapper<MissionPhotoEntity> {
    /**
     * 获取缩略图url分页列表
     * @param page
     * @param missionRecordId
     * @return
     */
    IPage<MissionPhotoEntity> getThumbnailPage(@Param("page") IPage<MissionPhotoEntity> page,
                                               @Param("missionRecordId") Integer missionRecordId,
                                               @Param("orgCode")String orgCode);

    /**
     * 通过图片id获取对应的架次id
     * @return
     */
    Integer getMissionIdById(Integer photoId);

    Integer getTaskIdByMissionId(Integer missionId);

    /**
     * 通过架次执行id获取全部原图url
     * @param recordId
     * @return
     */
    List<String> getPhotoUrlByRecordId(Integer recordId);

    /**
     * 通过idList获取原图url
     * @param idList
     * @return
     */
    List<String> getPhotoUrlByIdList(@Param("idList") List idList);

    /**
     * 通过图片原名，获取图片id
     * @param originalFilename
     * @return
     */
    @Select("SELECT id FROM mission_photo WHERE mission_records_id = #{recordId} AND original_filename = #{originalFilename} ")
    List<Long> getIdListByRecordIdAndName(@Param("recordId") Integer recordId, @Param("originalFilename") String originalFilename);

    /**
     * 通过机巢的图片id获取图片id
     * @param fileId
     * @return
     */
    @Select("SELECT id FROM mission_photo WHERE file_id = #{fileId}")
    Long getIdByFileId(String fileId);

    /**
     * 通过recordId获取fileIdList
     */
    @Select("SELECT file_id AS fileId FROM mission_photo WHERE mission_records_id = #{recordId} AND deleted = 0 ")
    List<String> getFileIdListByRecordId(Integer recordId);

    /**
     * 通过recordId获取照片
     */
    @Select("SELECT id FROM mission_photo WHERE mission_records_id = #{recordId} AND deleted = 0 ")
    List<Long> getPhotoByRecordId(Integer recordId);

    void deleteByRecordId(@Param("recordIdList")List<Integer> recordIdList);

    /**
     * 获取图片数量
     * @param recordId
     * @return
     */
    @Select("SELECT COUNT(id) FROM mission_photo WHERE mission_records_id = #{recordId} AND deleted = 0")
    Integer getPhotoNum(Integer recordId);


    /**
     * 根据tagId获取设备图片名称List
     * @param tagId
     * @return
     */
    List<String> getDeviceNameByTagId(@Param("tagId") Integer tagId,@Param("type") Integer type,@Param("defectStatus") Integer defectStatus,@Param("startTime")String startTime,@Param("endTime")String endTime);

    /**
     * 通过设备名称、tagId获取该设备的图片
     * @param name
     * @param tagId
     * @return
     */
    List<StationDevicePhotoDTO> getPhotoInfoList(@Param("name") String name, @Param("tagId") Integer tagId,@Param("type") Integer type,@Param("defectStatus") Integer defectStatus,@Param("startTime")String startTime,@Param("endTime")String endTime);
    
    /**
     * 通过设备名称、tagId获取该设备的图片,taskName取值与mission表
     * @param name
     * @param tagId
     * @return
     */
    List<StationDevicePhotoDTO> getPhotoInfoMissionRecordList(@Param("name") String name, @Param("tagId") Integer tagId,@Param("type") Integer type,@Param("defectStatus") Integer defectStatus,@Param("startTime")String startTime,@Param("endTime")String endTime);

    /**
     * 根据标签名字按月份查询
     * @param tagIds
     * @param names
     * @return
     */
    IPage<StationDefectPhotoDTO> getDefectMonthList(@Param("page")IPage<StationDefectPhotoDTO> page,@Param("tagIds") List<Integer> tagIds, @Param("names") List<String> names,@Param("missionRecordsIds") List<Integer> missionRecordsIds, @Param("defectStatus") Integer defectStatus, @Param("startTime")String startTime, @Param("endTime")String endTime,@Param("type")Integer type);

    /**
     * 根据标签、设备uid获取图片id、图片url
     * @param uuidList
     * @return
     */
    List<StationIdentifyPhotoDTO> getPhotoIdUrlList(@Param("tagId") Integer tagId, @Param("uuidList") List<String> uuidList);

    /**
     * 根据idList获取原图urlList
     * @param idList
     * @return
     */
    List<StationIdentifyPhotoDTO> getPhotoUrlList(@Param("idList") List<Long> idList);

    /**
     * 获取表计读数手动上传的图片
     * @param tagId
     * @param uuid
     * @param startTime
     * @param endTime
     * @return
     */
    List<Double> getMeterPhotoNumList(@Param("tagId") Integer tagId, @Param("uuid") String uuid,
                                      @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 首页-成果统计-获取用户对应的图片总数、视频总数
     * @param startTime
     * @param endTime
     * @return
     */
    Map getTotalPhotoAndVideo(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("orgCode")String orgCode);

    /**
     * 首页-成果统计-获取用户每个标签对应的图片总数
     * @param startTime
     * @param endTime
     * @return
     */
    List<MissionPhotoVideoTotalDTO.PhotoTagBean> getTotalPhotoByTag(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("orgCode")String orgCode);

    /**
     * 首页-成果统计-获取用户每个标签对应的视频总数
     * @param startTime
     * @param endTime
     * @return
     */
    List<MissionPhotoVideoTotalDTO.VideoTagBean> getTotalVideoByTag(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("orgCode")String orgCode);

    /**
     * 首页-成果统计-根据tagId获取用户的机巢的图片数量TOP5(机巢)
     * @param startTime
     * @param endTime
     * @param orgCode
     * @param userId
     * @param nestId
     * @return
     */
    List<MissionRecordsTotalDTO> getTotalNestPhotoByTag(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("orgCode")String orgCode, @Param("userId")Long userId, @Param("nestId")String nestId);

    /**
     * 首页-成果统计-根据tagId获取用户的机巢的视频数量TOP5（机巢）
     * @param startTime
     * @param endTime
     * @param orgCode
     * @param userId
     * @param nestId
     * @return
     */
    List<MissionRecordsTotalDTO> getTotalNestVideoByTag(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("orgCode")String orgCode, @Param("userId")Long userId, @Param("nestId")String nestId);

    /**
     * 首页-成果统计-根据机巢id分页获取机巢的成果图片List(机巢)
     * @param page
     * @param startTime
     * @param endTime
     * @param nestId
     * @return
     */
    IPage<MissionPhotoVideoTotalDTO.PhotoNestPageBean> getPhotoByNestPageList(IPage<MissionPhotoVideoTotalDTO.PhotoNestPageBean> page,
                                                                              @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("nestId")String nestId);

    /**
     * 首页-成果统计-根据机巢id分页获取机巢的成果图片List(移动终端)
     * @param page
     * @param startTime
     * @param endTime
     * @param appId
     * @return
     */
    IPage<MissionPhotoVideoTotalDTO.PhotoNestPageBean> getPhotoByAppPageList(IPage<MissionPhotoVideoTotalDTO.PhotoNestPageBean> page,
                                                                             @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("appId")String appId);

    /**
     * 首页-成果统计-根据机巢id分页获取机巢的成果视频List(机巢)
     * @param page
     * @param startTime
     * @param endTime
     * @param nestId
     * @return
     */
    IPage<MissionPhotoVideoTotalDTO.VideoNestPageBean> getVideoByNestPageList(IPage<MissionPhotoVideoTotalDTO.VideoNestPageBean> page,
                                                                              @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("nestId")String nestId);

    /**
     * 首页-成果统计-根据机巢id分页获取机巢的成果视频List(移动终端)
     * @param page
     * @param startTime
     * @param endTime
     * @param appId
     * @return
     */
    IPage<MissionPhotoVideoTotalDTO.VideoNestPageBean> getVideoByAppPageList(IPage<MissionPhotoVideoTotalDTO.VideoNestPageBean> page,
                                                                             @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("appId")String appId);

    /**
     * 通过架次执行id获取图片,按照片拍摄时间排序
     * @param recordId
     * @return
     */
    @Select("SELECT id FROM mission_photo WHERE mission_records_id = #{recordId} AND deleted = 0 ORDER BY create_time")
    List<Long> getIdByRecordId(Integer recordId);

    /**
     * 通过CheckPointUuid获取name
     * @param uuid
     * @return
     */
    @Select("SELECT `name` AS checkPointName FROM station_checkpoint WHERE uuid = #{uuid}")
    String getCheckPointNameByUuid(@Param("uuid") String uuid);

    List<Integer> selectAllDefectPhoto(@Param("photoDeviceName") String photoDeviceName, @Param("tagId") Integer tagId);

    List<MissionPhotoEntity> getPhotoByMissionIds(@Param("missionIdList") List<Integer> missionIdList, @Param("startTimeStr") String startTimeStr, @Param("endTimeStr") String endTimeStr);

    List<MissionPhotoEntity> getPhotoByTagIds(@Param("tagIdList") List<Integer> tagIdList);

    List<MissionPhotoEntity> getPhotoByType(@Param("type") Integer type,@Param("startTimeStr") String startTimeStr,@Param("endTimeStr") String endTimeStr);

    @Select("SELECT `value` FROM `station_infrared_threshold` where `name` = '表记阈值'")
    Double getThreshold();

    @Update("update `station_infrared_threshold` set `value` = #{value} where `name` = '表记阈值'")
    void setThreshold(String value);

    @Select("select mt.type from mission_photo m,mission_photo_type_rel mt where m.id = mt.mission_photo_id and mt.deleted = 0 and m.deleted = 0 and m.mission_id = #{missionId} and m.mission_records_id = #{recordId}")
    Integer getRecordType(Integer missionId);

    @Select("select m.id from mission_photo m left join station_identify_record s on m.id = s.photo_id where m.mission_id = #{missionId} and m.mission_records_id = #{recordId} and m.deleted = 0 and s.id is null")
    List<Integer> getIdentifyRecord(@Param("missionId")Integer missionId, @Param("recordId")Integer recordId);

    List<Map> getRecordMap(@Param("missionId")Integer missionId, @Param("recordId")Integer recordId);

    @Select("select m.id from mission_photo m where m.mission_id = #{missionId} and m.mission_records_id = #{recordId} and m.deleted = 0")
    List<Integer> getMediaPhoto(Integer missionId, Integer recordId);

    /**
     * 获取有图片成果的taskId
     * @param taskIds
     * @param startTimeStr
     * @param endTimeStr
     * @return
     */
    List<MissionPhotoEntity> getTaskWithMissionPhotoByTaskIds(@Param("taskIds") List taskIds, @Param("startTimeStr") String startTimeStr, @Param("endTimeStr") String endTimeStr);

    List<MissionPhotoEntity> getUnDetectTaskWithMissionPhotoByTaskIds(@Param("taskIds") List taskIds, @Param("startTimeStr") String startTimeStr, @Param("endTimeStr") String endTimeStr);

    Map getDefectStatistics(@Param("startTime")String startTime,@Param("endTime")String endTime,@Param("tagList")List<Integer> tagList);

    List<MissionPhotoEntity> getMissionPhotos();

    List<TaskAndMissionRecordsDto> getDeviceNameByTagIdAndTask(@Param("tagId") Integer tagId, @Param("type") Integer type, @Param("defectStatus") Integer defectStatus, @Param("startTime")String startTime, @Param("endTime")String endTime);

    IPage<StationDefectPhotoDTO> getDefectByTaskId(@Param("page") IPage<StationDefectPhotoDTO> page, @Param("missionRecordsIds") List<Integer> missionRecordsIds, @Param("tagIds") List<Integer> tagIds, @Param("taskIds") List<Integer> taskIds, @Param("type") Integer type, @Param("defectStatus") Integer defectStatus, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<StationDevicePhotoDTO> getRiverByTaskId(@Param("missionRecordsId") Integer missionRecordsId, @Param("tagId") Integer tagId, @Param("taskId") Integer taskId, @Param("type") Integer type);

    List<MissionRecordsEntity> getMissionRecords(@Param("startTime") String startTime, @Param("endTime") String endTime);

    List<Map> getInspect(@Param("missionRecordsIds")List<Integer> missionRecordsIds);

    /**
     * 获取照片和问题类型
     * @param photoIds
     * @return
     */
    List<Map<String, Object>> getPhotoIdAndDefectTypeId(@Param("photoIds") List<String> photoIds);

    List<MissionPhotoEntity> getMissionPhotoByIds(@Param("tagId")Integer tagId, @Param("type")Integer type, @Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("SELECT source FROM mission_photo WHERE id = #{id} ")
    Integer getPhotoSource(Long id);

    /**
     * 获取照片、标签、问题来源等信息
     * @param problemStatus
     * @param startTime
     * @param endTime
     * @param problemSourceList
     * @param tagId
     * @return
     */
    List<PhotoTagSourceDTO> getPhotoTagSourceList(@Param("problemStatus") Integer problemStatus, @Param("problemSourceList") List<Integer> problemSourceList,
                                                  @Param("startTime") String startTime, @Param("endTime") String endTime,
                                                  @Param("tagId") Integer tagId);

    List<StationDefectPhotoDTO> getAllStationDefectPhotoDTO1(@Param("type") Integer type, @Param("tagId") Long id, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("defectStatus") Integer defectStatus);


    /**
     * 查询不等于tagVersion,前500条数据的记录
     *
     * @param tagVersion 标记版本
     * @return {@link List}<{@link MissionPhotoEntity}>
     */
    List<MissionPhotoEntity> queryListByTagVersion(@Param("tagVersion") int tagVersion);

    /**
     * 查询已删除数据
     *
     * @param tagVersion 标记版本
     * @return {@link List}<{@link MissionPhotoEntity}>
     */
    List<MissionPhotoEntity> queryDeleteList(@Param("tagVersion") int tagVersion);

    /**
     * 更新物理删除
     *
     * @param idList id列表
     * @return int
     */
    int updatePhysicsDelete(@Param("idList") Collection<Long> idList);

    /**
     * 查询每个架次的图片数量
     *
     * @param missionRecordIdList 任务id列表
     * @return {@link List}<{@link Map}<{@link String}, {@link Long}>>
     */
    List<Map<String, Object>> queryPhotoNum(@Param("missionRecordIdList") List<Integer> missionRecordIdList);

    List<MissionPhotoEntity> queryPhotos(@Param("photoIds") List<Long> photoIds);

    List<MissionPhotoPointOutDTO> getMavic3PhotoByPoint(@Param("start") Integer startIndex, @Param("end") Integer endIndex, @Param("recordId") String missionRecordsId, @Param("type") int type);
}
