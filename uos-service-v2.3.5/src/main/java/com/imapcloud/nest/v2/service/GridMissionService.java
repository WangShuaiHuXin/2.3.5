package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.GridMissionDTO;
import com.imapcloud.nest.v2.service.dto.in.GridHistoryInDTO;
import com.imapcloud.nest.v2.service.dto.in.GridMissionRecordPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.GridMissionRecordPageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;

import java.util.List;

/**
 * @Classname GridMissionService
 * @Description 网格任务相关接口（二期
 * @Date 2022/12/18 11:10
 * @Author Carnival
 */
public interface GridMissionService {

    /**
     * 根据管理网格ID获取网格航点缩略图
     */
    List<GridOutDTO.PhotoDTO> queryPhotoByGridIds(List<String> gridIds, String gridInspectId, Integer missionRecordsId, String orgCode);

    /**
     * 关联照片与网格
     */
    Boolean relPhotoToGrid(List<Long> photoIdList, Integer taskId, Integer missionRecordId, String orgCode);

    /**
     * 根据管理网格ID获取巡检列表
     */
    List<GridOutDTO.InspectRecordDTO> queryInspectByGridIds(String gridManageId);

    /**
     * 根据管理网格ID获取统计记录
     */
    List<GridOutDTO.GridStatisticsDTO> queryGridStatistics(List<String> gridManageIds);

    /**
     * 根据数据网格ID获取历史照片
     */
    List<GridOutDTO.Photo> queryHistoryByGridDataId(GridHistoryInDTO dto);

    /**
     * 任务下发保存记录
     */
    boolean saveMissionToGrid(GridMissionDTO gridMissionDTO);

    /**
     * 立即巡检保存巡检记录
     */
    boolean saveGridInspect(GridMissionDTO gridMissionDTO);

    /**
     * 分页获取网格记录信息
     */
    PageResultInfo<GridMissionRecordPageOutDTO> pageGridMissionRecord(GridMissionRecordPageInDTO dto);

    /**
     * 判断管理网格是否有数据
     */
    List<GridOutDTO.GridManageHasDataDTO> gridManageHasData(List<String> gridManageIds, String startTime, String endTime);


    /**
     * 查询任务状态
     */
    List<GridOutDTO.MissionStatusDTO> queryMissionStatus(String gridMangeId, String gridInspectId, String orgCode);

    /**
     * 修改巡检记录是否最新状态
     */
    void updateIsNewestInspect(List<Integer> taskIds);

}
