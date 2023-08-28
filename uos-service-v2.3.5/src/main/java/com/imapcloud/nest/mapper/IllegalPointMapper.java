package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.DefectPhotoDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TagOrthoDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TagPointCloudDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TaskPhotoDTO;
import com.imapcloud.nest.pojo.vo.IllegalPointVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 违建点信息表 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2021-03-17
 */
public interface IllegalPointMapper extends BaseMapper<IllegalPointEntity> {


    /**
     * 根据经纬度获取匹配精确范围内，经度匹配的上的照片路径
     * @param photoIdList
     * @param latitude
     * @param longitude
     * @param relRangeLat 精确误差范围
     * @return
     */
    List<MissionPhotoEntity> getCloseLatitudePhoto(@Param("photoIdList")List<Long> photoIdList, @Param("latitude") Double latitude, @Param("longitude") Double longitude,
                                                   @Param("relRangeLat") Double relRangeLat, @Param("relRangeLon") Double relRangeLon);

    /**
     * 根据经度获取纬度误差范围最小的照片路径
     * @param latitude
     * @param longitude
     * @return
     */
    MissionPhotoEntity getCloseLongitudePhoto(@Param("latitude") Double latitude, @Param("longitude") Double longitude);

    /**
     * 根据标签和类型，获取全部违建点
     * @param tagId
     * @param type
     * @return
     */
    List<IllegalPointEntity> getIllegalListByTag(@Param("tagId") Integer tagId, @Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 分析中台-违建识别-标签点云列表
     * @param unitId
     * @return
     */
    List<TagPointCloudDTO> getTagPointCloudList(@Param("orgCode") String orgCode);

    /**
     * 分析中台-违章建筑-标签正射列表
     * @param unitId
     * @return
     */
    List<TagOrthoDTO> getTagOrthoList(@Param("orgCode") String orgCode, @Param("problemSource")Integer problemSource);

    /**
     * 获取在历史照片范围内的照片idList
     * @param problemSource
     * @param tagId
     */
    List<Long> getIllegalPhotoIdList(@Param("problemSource") Integer problemSource, @Param("tagId") Integer tagId);

    /**
     *  获取同名照片的历史照片信息
     * @param missionRecordId
     * @param photoIdList
     * @param name
     * @param photoId
     * @param latitude
     * @param longitude
     * @param relRangeLat
     * @param relRangeLon
     * @param startTime
     * @param endTime
     * @return
     */
    List<TaskPhotoDTO> getHistoryPhotoInfo( @Param("missionRecordId") Integer missionRecordId, @Param("photoIdList") List<Long> photoIdList,
                                            @Param("name") String name, @Param("photoId") Long photoId,
                                            @Param("latitude") Double latitude, @Param("longitude") Double longitude,
                                            @Param("relRangeLat") Double relRangeLat, @Param("relRangeLon") Double relRangeLon,
                                            @Param("startTime") String startTime, @Param("endTime") String endTime);

//    /**
//     * 获取点云关联的照片
//     * @param afterFileId
//     * @param dataType
//     * @return
//     */
//    List<TaskPhotoDTO> getPointCloudRelatedPhoto(@Param("afterFileId") Integer afterFileId, @Param("dataType") Integer dataType);
//
//    /**
//     * 获取正射关联的照片
//     * @param afterFileId
//     * @param dataType
//     * @return
//     */
//    List<TaskPhotoDTO> getOrthoRelatedPhoto(@Param("afterFileId") Integer afterFileId, @Param("dataType") Integer dataType);

    /**
     * 获取点云或正射关联的照片
     * @param afterFileId
     * @param dataType
     * @return
     */
    List<TaskPhotoDTO> getRelatedPhoto(@Param("afterFileId") Integer afterFileId, @Param("dataType") Integer dataType);

    /**
     * 获取同任务下的架次记录idList
     * @param taskId
     * @param dataType
     * @param missionRecordsId
     * @return
     */
    List<Integer> getRecordsIdBySameTask(@Param("taskId") Integer taskId, @Param("dataType") Integer dataType, @Param("missionRecordsId") Integer missionRecordsId);

    /**
     * 获取同任务下其他架次已推送分析的点云的idList
     * @param taskId
     * @param dataId
     * @param problemSource
     * @return
     */
    List<Integer> getPointCloudIdByTaskId(@Param("taskId") Integer taskId, @Param("dataId") Integer dataId, @Param("problemSource") Integer problemSource);
    List<Integer> getOrthoIdByTaskId(@Param("taskId") Integer taskId, @Param("dataId") Integer dataId, @Param("problemSource") Integer problemSource);
}
