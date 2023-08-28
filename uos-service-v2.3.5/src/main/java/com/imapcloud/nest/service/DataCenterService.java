package com.imapcloud.nest.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.DataAirEntity;
import com.imapcloud.nest.model.DataCenterEntity;
import com.imapcloud.nest.model.MissionVideoEntity;
import com.imapcloud.nest.model.SysTagEntity;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.reqDto.DataReqDto;
import com.imapcloud.nest.pojo.dto.reqDto.IdenDataDto;
import com.imapcloud.nest.v2.service.dto.out.DataScenePhotoOutDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author zheng
 */
public interface DataCenterService extends IService<DataCenterEntity> {

    List<SysTagEntity> getTags(Integer dataType, String startTime, String endTime);

    JSONArray getData(Integer dataType, Integer missionRecordsId);

    DataTotalDTO getTotal(String startTime, String endTime);

    Map getInspect(String startTime, String endTime);

    IPage<MissionRecordsDto> getTask(Map<String, Object> params, Integer tagId, Integer dataType, String name);

    void saveTask(TaskDataDto taskDataDto);

    DataScenePhotoOutDTO.PushOut push(IdenDataDto idenDataDto);

    void pushData(IdenDataDto idenDataDto);

    void delTask(DataReqDto dataReqDto);

    void changeName(Integer missionRecordsIds, String name);

    IPage getDataPage(Integer page, Integer limit, Integer dataType, Integer missionRecordsId, String startTime, String endTime);

    void delData(DataReqDto dataReqDto);

    /**
     * 照片、视频、污染网格的下载
     * @param dataType
     * @param missionRecordIdList
     * @param response
     */
    void downloadByRecordIds(Integer dataType, List<Integer> missionRecordIdList, HttpServletResponse response);

    /**
     * 正射点云倾斜矢量的下载
     * @param dataType
     * @param missionRecordId
     * @param response
     */
    void downloadData(Integer dataType, Integer missionRecordId, HttpServletResponse response);

    void setSrtJson(MissionVideoEntity missionVideoEntity);

    void setFirstPoint(MissionVideoEntity missionVideoEntity);

    List<Map> getTaskMissions(Integer tagId, Integer dataType, String name);

    void relatedMissionRecordIds(Integer dataType, Integer dataId, Integer missionRecordId, List<Integer> photoId);

    void deletedRelatedMissionRecordIds(Integer dataId, Integer dataType);

    void updateMission(TaskDataDto taskDataDto);

    void downloadSrt(Integer videoId, HttpServletResponse response);

    void copyTaskMission(TaskMissionDto taskMissionDto);

    void importAirExcel(MultipartFile multipartFile, Integer missionRecordsId);

    Boolean delTag(Integer tagId);

    List<DataAirEntity> getDataAir(Integer missionRecordsId);

    void deletedDataAir(List<Integer> dataAirIdList);

    void saveDataAir(DataAirDto dataAirDto);

    void exportDataAir(String dataStr, HttpServletResponse response);
    void pollutionGridRename(Integer pollutionGridId, String name);

    void delServerFile(String startTime,String endTime);

    Map getServerSize();

    /**
     * 查询指定类型，架次记录id的单位code
     *
     * @param dataType        数据类型
     * @param missionRecordId 任务记录id
     * @return {@link String}
     */
    String getOrgCode(Integer dataType, Integer missionRecordId);
}
