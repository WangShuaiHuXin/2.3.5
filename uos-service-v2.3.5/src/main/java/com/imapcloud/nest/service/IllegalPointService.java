package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.DataPointCloudEntity;
import com.imapcloud.nest.model.IllegalPointEntity;
import com.imapcloud.nest.pojo.dto.DefectPhotoDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.DeleteDataDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.IllegalPointMarkDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TaskPhotoDTO;
import com.imapcloud.nest.utils.RestRes;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 违建点信息表 服务类
 * </p>
 *
 * @author zheng
 * @since 2021-03-17
 */
public interface IllegalPointService extends IService<IllegalPointEntity> {

    /**
     * 根据点云id，获取违建列表
     * @param beforeFileId
     * @param afterFileId
     * @return
     */
    RestRes getIllegalPointList(Integer beforeFileId, Integer afterFileId, Integer problemSource);
    RestRes getHZIllegalPointList(Integer problemSource);

    /**
     *  添加违建点，上传违建图片
     * @param illegalPointEntityList
     * @return
     */
    RestRes saveIllegalPoint(List<IllegalPointEntity> illegalPointEntityList);

    /**
     * 编辑违建点
     * @param illegalPointEntity
     * @return
     */
    RestRes updateIllegalPoint(IllegalPointEntity illegalPointEntity);
    /**
     * 删除违建点
     * @param idList
     * @return
     */
    RestRes deleteIllegalPoint(List idList);

    /**
     * 智能分析
     * @return
     */
    RestRes smartAnalysis(Integer beforeFileId, Integer afterFileId, String threshold, String kmlUrl);

    /**
     * 终止智能分析
     * @param beforeFileId
     * @param afterFileId
     * @return
     */
    RestRes stopSmartAnalysis(Integer beforeFileId, Integer afterFileId);


    /**
     * 根据图片经纬度匹配照片
     * @param latitude
     * @param longitude
     * @param range
     * @return
     */
//    RestRes getRangePhoto(Integer id, Double latitude, Double longitude, Double range);
    RestRes autoMatchPhoto(Integer id, Integer dataType, Integer problemSource, Integer tagId, Double latitude, Double longitude, Double range);

    /**
     * 根据标签和类型，获取全部违建点
     * @param tagId
     * @param type
     * @return
     */
    List<IllegalPointEntity> getIllegalListByTag(Integer tagId, Integer type, String startTime, String endTime);

    /**
     * 分析中台-违建识别-标签点云列表
     * @return
     */
    Map<String, Object> getTagPointCloudList();

    /**
     * 分析中台-违章建筑-标签正射列表
     * @return
     */
    Map<String, Object> getTagOrthoList(Integer problemSource);

    /**
     * 获取历史照片
     * @param photoId
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String, Object> getHistoryPhotoList(Integer dataId, Integer taskId, Long photoId, Integer dataType, Integer problemSource, Integer tagId, Double range, String startTime, String endTime);

    /**
     * 获取点云关联的照片
     * @param afterFileId
     * @return
     */
    Map<String, Object> getRelatedPhoto(Integer afterFileId, Integer dataType);

//    /**
//     * 标记违建点照片
//     * @deprecated 2.2.3，违建点相关代码已废弃，将在后续版本删除
//     */
//    @Deprecated
//    void markPhoto(IllegalPointMarkDTO illegalPointMarkDTO);

    /**
     * 删除违建、违章模块的点云，正射及其关联表
     * @param deleteDataDTO
     */
    void deleteDataList(DeleteDataDTO deleteDataDTO);



    /**
     * 多光谱识别
     * @param id
     * @param type
     * @return
     */
    RestRes analysFeatures(Integer id,Integer type);

    RestRes analysTest();

    RestRes analysWater(Integer id);

    void downLoadWater(Integer id, HttpServletResponse response);

//    /**
//     * @deprecated 2.2.3，已跟前端确认，该接口未在使用，将在后续版本删除
//     */
//    @Deprecated
//    void delFeatures(Integer id);
//    /**
//     * @deprecated 2.2.3，已跟前端确认，该接口未在使用，将在后续版本删除
//     */
//    @Deprecated
//    void delWater(Integer id);

    Map getTagOrthoRecordList(Integer problemSource,String recordMonth,String recordDay);
}
