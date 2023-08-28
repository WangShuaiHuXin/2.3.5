package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.DataProblemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.pojo.dto.DefectPhotoDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.PhotoIdNameTagIdDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TaskPhotoDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TaskRecordPhotoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zhengxd
 * @since 2021-06-16
 */
public interface DataProblemMapper extends BaseMapper<DataProblemEntity> {

    /**
     * 获取任务-有问题照片信息List
     *
     * @param problemSourceList
     * @param tagId
     * @param startTime
     * @param endTime
     * @return
     */
    List<TaskPhotoDTO> getTaskProblemPhotoList(@Param("tagId") Integer tagId, @Param("problemSourceList") List<Integer> problemSourceList,
                                              @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<TaskPhotoDTO> getHZTaskProblemPhotoList(@Param("problemSource") Integer problemSource,
                                               @Param("startTime") String startTime, @Param("endTime") String endTime);
    
    /**
     * 获取任务-有问题的电力照片信息List
     *
     * @param problemSourceList
     * @param tagId
     * @param startTime
     * @param endTime
     * @return
     */
    List<TaskPhotoDTO> getTaskProblemPhotoListElectric(@Param("tagId") Integer tagId, @Param("problemSourceList") List<Integer> problemSourceList,
                                              @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获取任务-问题照片信息List
     *
     * @param problemSource
     * @param tagId
     * @param startTime
     * @param endTime
     * @return
     */
    List<TaskRecordPhotoDTO> getTaskPhotoList(@Param("tagId") Integer tagId, @Param("problemSource") Integer problemSource,
                                              @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据架次记录idList获取照片信息List
     * @param tagIdList
     * @param problemSource
     * @param missionRecordIdList
     * @return
     */
    List<TaskPhotoDTO> getPhotoListByMissionRecordId(@Param("missionRecordIdList") List<Integer> missionRecordIdList,
                                                     @Param("tagIdList") List<Integer> tagIdList, @Param("problemSource") Integer problemSource,
                                                     @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据架次记录idList获取照片id、名称、标签id
     * @param missionRecordIdList
     * @param tagIdList
     * @param problemSource
     * @param startTime
     * @param endTime
     * @return
     */
    List<PhotoIdNameTagIdDTO> getPhotoIdNameTagId(@Param("missionRecordIdList") List<Integer> missionRecordIdList,
                                                  @Param("tagIdList") List<Integer> tagIdList, @Param("problemSource") Integer problemSource,
                                                  @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据架次记录idList分页获取照片信息List
     * @param tagIdList
     * @param problemSource
     * @param missionRecordIdList
     * @return
     */
    IPage<TaskPhotoDTO> getPhotoListByMissionRecordId(@Param("page") IPage<TaskPhotoDTO> page, @Param("missionRecordIdList") List<Integer> missionRecordIdList,
                                                          @Param("tagIdList") List<Integer> tagIdList, @Param("problemSource") Integer problemSource,
                                                          @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据照片id获取照片和任务信息
     * @param photoId
     * @return
     */
    TaskPhotoDTO getPhotoTaskInfoByPhotoId(Long photoId);

    /**
     *  获取同名照片的历史照片信息
     * @param missionRecordIdList
     * @param problemSource
     * @param photoId
     * @param latitude
     * @param longitude
     * @param relRangeLat
     * @param relRangeLon
     * @param startTime
     * @param endTime
     * @return
     */
    List<TaskPhotoDTO> getHistoryPhotoInfo(@Param("missionRecordIdList") List<Integer> missionRecordIdList,
                                                      @Param("problemSource") Integer problemSource, @Param("photoId") Long photoId,
                                                      @Param("latitude") Double latitude, @Param("longitude") Double longitude,
                                                      @Param("relRangeLat") Double relRangeLat, @Param("relRangeLon") Double relRangeLon,
                                                      @Param("startTime") String startTime, @Param("endTime") String endTime);



}
