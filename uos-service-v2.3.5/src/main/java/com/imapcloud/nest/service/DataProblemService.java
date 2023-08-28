package com.imapcloud.nest.service;

import com.imapcloud.nest.model.DataProblemEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.pojo.dto.DefectPhotoDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.DeleteDataDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TaskPhotoDTO;
import com.imapcloud.nest.utils.RestRes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhengxd
 * @since 2021-06-16
 */
public interface DataProblemService extends IService<DataProblemEntity> {
    /**
     * 获取分析应用的 任务-有问题List
     * @param tagId 标签id
     * @param problemSourceList 问题来源
     * @param startTime
     * @param endTime
     * @return
     */
    RestRes getTaskProblemPhotoList(Integer tagId, List<Integer> problemSourceList, String startTime, String endTime);
    RestRes getHZTaskProblemPhotoList(Integer problemSource, String startTime, String endTime);

    /**
     * 获取分析应用的 任务-变电站-有问题List
     * @param tagId
     * @param problemSourceList
     * @param startTime
     * @param endTime
     * @return
     */
    RestRes getTaskProblemPhotoListElectric(Integer tagId, List<Integer> problemSourceList, String startTime,
			String endTime);

    List<TaskPhotoDTO> getTagProblemList(Integer tagId, List<Integer> problemSourceList, String startTime, String endTime);
    /**
     * 获取分析中台的 任务-问题数据List
     * @param tagId 标签id
     * @param problemSource 问题来源
     * @param startTime
     * @param endTime
     * @return
     */
    RestRes getTaskPhotoList(Integer tagId ,Integer problemSource, String startTime, String endTime);

    /**
     * 根据架次记录idList获取照片-问题信息List
     * @param missionRecordIdList
     * @param tagIdList
     * @param problemSource
     * @param startTime
     * @param endTime
     * @return
     */
    RestRes getPhotoProblemList(List<Integer> missionRecordIdList, List<Integer> tagIdList, Integer problemSource, String startTime, String endTime);

    /**
     * 下载、删除、重置、AI识别时获取照片id、名称、标签id使用
     * @param missionRecordIdList
     * @param tagIdList
     * @param problemSource
     * @param startTime
     * @param endTime
     * @return
     */
    RestRes getPhotoIdNameTagIdList(List<Integer> missionRecordIdList, List<Integer> tagIdList, Integer problemSource, String startTime, String endTime);

    /**
     * 根据架次记录idList分页获取照片-问题信息List
     * @param pageNum
     * @param pageSize
     * @param missionRecordIdList
     * @param tagIdList
     * @param problemSource
     * @param startTime
     * @param endTime
     * @return
     */
    RestRes getPhotoProblemPageList(Integer pageNum, Integer pageSize, List<Integer> missionRecordIdList, List<Integer> tagIdList, Integer problemSource, String startTime, String endTime);

    /**
     * 批量解决问题
     * @param problemIdList
     * @return
     */
    RestRes solveProblem(List<Integer> problemIdList);

    /**
     * 分析中台删除
     * @param deleteDataDTO
     * @return
     */
    RestRes deleteDataProblem(DeleteDataDTO deleteDataDTO);

    /**
     * 分析中台重置
     * @param deleteDataDTO
     * @return
     */
    RestRes resetDataProblem(DeleteDataDTO deleteDataDTO);

    /**
     * 获取同名照片的历史照片信息和问题信息
     * @param problemSource 问题类型
     * @param photoId 照片id
     * @param startTime
     * @param endTime
     * @return
     */
    RestRes getHistoryPhotoInfo(Integer problemSource, Long photoId, Double range, String startTime, String endTime);

    /**
     * 获取历史照片信息（不包含问题）
     * @param problemSource
     * @param photoId
     * @param startTime
     * @param endTime
     * @return
     */
    List<TaskPhotoDTO> getHistoryPhotoInfoList(Integer problemSource, Long photoId, Double range, String startTime, String endTime);

}
