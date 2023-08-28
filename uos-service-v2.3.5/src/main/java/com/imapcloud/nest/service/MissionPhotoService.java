package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.pojo.dto.MeterReadDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.PhotoTagSourceDTO;
import com.imapcloud.nest.pojo.dto.reqDto.DefectMonthListReqDto;
import com.imapcloud.nest.pojo.dto.reqDto.MissionPhotosReqDto;
import com.imapcloud.nest.pojo.dto.reqDto.RecordDto;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.StationDefectPhotoDTO;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.dto.out.MissionPhotoPointOutDTO;
import com.imapcloud.sdk.pojo.BaseResult3;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface MissionPhotoService extends IService<MissionPhotoEntity> {

    /**
     * CPS回写动作
     *
     * @param mediaFile
     * @return
     */
    RestRes uploadPhotoCps(String mediaFile);


    /**
     * web端同步数据源 V1
     *
     * @param recordId
     * @return
     */
    RestRes getPhoto(Integer recordId, String nestUuid,String evn);

    /**
     * 批量同步数据
     *
     * @param recordIdList
     * @return
     */
    RestRes batchTranData(String nestId, List<Integer> recordIdList);

    RestRes cancelBatchTranData(String nestId, Integer recordId);

    /**
     * 获取缩略图url分页列表
     *
     * @param page
     * @param missionRecordId
     * @return
     */
    RestRes getThumbnailPage(Map<String, Object> page, Integer missionRecordId, Integer airLineId);

    /**
     * 下载图片
     *
     * @param ids              要下载的图片ids
     * @param isAll            是否选中全部图片下载
     * @param missionRecordsId 架次执行id
     * @return
     */
    void downlandPic(String ids, boolean isAll, Integer missionRecordsId, HttpServletResponse response);

    /**
     * 删除图片
     *
     * @param recordIdList
     */
    void deletePhoto(List<Integer> recordIdList);

    /**
     * 获取图片数量
     *
     * @param recordId
     * @return
     */
    Integer getPhotoNum(Integer recordId);

    /**
     * 校验同步源数据是否全部成功，并对应修改dataStatus值
     */
    Boolean checkGetMediaSuccess(Integer missionId, Integer recordId, Integer successType, Integer errorType);

    /**
     * 获取未识别的数据
     */
    List<Integer> getMediaRecordSuccess(Integer missionId, Integer recordId);

    /**
     * 获取自动识别后的数据
     *
     * @param missionId
     * @param recordId
     * @return
     */
    List<Map> getRecordMap(Integer missionId, Integer recordId);

    /**
     * 获取识别类型
     */
    Integer getRecordType(Integer missionId);

    /**
     * 暂停同步源数据
     *
     * @return
     */
    RestRes stopGetMedia(String nestUuid, Integer recordId);

    /**
     * 获取移动终端的任务架次记录分页列表
     *
     * @param appId
     * @return
     */
    PageUtils getAppMissionRecordPage(Integer page, Integer limit, String appId, String taskName);

    /**
     * 获取缺陷识别的数目统计
     *
     * @return
     */
    RestRes getDefectStatistics(String startTime, String endTime);

    /**
     * 删除成果的图片
     *
     * @param reqDtoList
     * @return
     */
    RestRes deleteMissionPhoto(List<MissionPhotosReqDto> reqDtoList);

    /**
     * 修改成果缺陷的状态
     *
     * @return
     */
    RestRes updatePhotoDefectStatus(List<Integer> photoIds, Integer defectStatus);

    /**
     * 修改图片名称
     *
     * @param id
     * @param photoName
     * @return
     */
    RestRes updatePhotoName(Long id, String photoName);

    /**
     * 获取表计读数图片分页列表
     *
     * @param name
     * @param missionRecordId
     * @param pageNum
     * @param pageSize
     * @return
     */
    RestRes getPhotoListPage(Integer tagId, String name, String missionRecordId, Integer defectStatus, Integer pageNum, Integer pageSize);

    /**
     * 获取表计读数的全部数值
     *
     * @param meterReadDTO
     * @return
     */
    RestRes getNumberList(MeterReadDTO meterReadDTO);

    /**
     * 根据tagId获取设备列表 和 设备最后一张图片的表计读数信息
     *
     * @param tagId
     * @param type
     * @param defectStatus
     * @param startTime
     * @param endTime
     * @param flag
     * @return
     */
    RestRes getDeviceList(Integer tagId, List<Integer> type, Integer defectStatus, String startTime, String endTime, Integer flag);

    /**
     * 根据tagId获取任务列表
     *
     * @param tagId
     * @param type
     * @param defectStatus
     * @param startTime
     * @param endTime
     * @return
     */
    RestRes getMissionList(Integer tagId, Integer type, Integer defectStatus, String startTime, String endTime);

    /**
     * 首页-成果统计-获取用户对应的图片总数、视频总数
     *
     * @return
     */
    RestRes getTotalPhotoAndVideo(String startTime, String endTime);


    /**
     * 首页-成果统计-根据tagId获取用户的机巢的图片数量TOP5
     *
     * @param startTime
     * @param endTime
     * @param nestId
     * @return
     */
    RestRes getTotalNestPhotoByTag(String startTime, String endTime, String nestId);

    /**
     * 首页-成果统计-根据tagId获取用户的机巢的视频数量TOP5
     *
     * @param startTime
     * @param endTime
     * @param nestId
     * @return
     */
    RestRes getTotalNestVideoByTag(String startTime, String endTime, String nestId);

    /**
     * 首页-成果统计-根据机巢id分页获取机巢的成果图片List
     *
     * @param startTime
     * @param endTime
     * @param nestId
     * @param appId
     * @param pageNum
     * @param pageSize
     * @return
     */
    RestRes getPhotoByNestPageList(String startTime, String endTime, String nestId, String appId, Integer pageNum, Integer pageSize);

    /**
     * 首页-成果统计-根据机巢id分页获取机巢的成果视频List
     *
     * @param startTime
     * @param endTime
     * @param nestId
     * @param appId
     * @param pageNum
     * @param pageSize
     * @return
     */
    RestRes getVideoByNestPageList(String startTime, String endTime, String nestId, String appId, Integer pageNum, Integer pageSize);

    /**
     * 成果管理-航线照片重命名, push=true会自动推送到综合和电力
     *
     * @param airLineId
     * @param missionRecordId
     * @param push
     * @return
     */
    RestRes updateAirlinePhotoName(Integer airLineId, Integer missionRecordId, boolean push);

    /**
     * 成果管理-航线照片重命名, push=true
     *
     * @param airLineId
     * @param missionRecordId
     * @return
     */
    RestRes updateAirlinePhotoName(Integer airLineId, Integer missionRecordId);

    /**
     * 照片重命名，push=true的话，会推送综合和电力
     *
     * @param airLineId       空气管路id
     * @param missionRecordId 任务记录id
     * @param push            推
     * @return {@link RestRes}
     */
    RestRes pushAnalysis2(Integer airLineId, Integer missionRecordId, boolean push);

    RestRes resetPhotoRecord(RecordDto recordDto);

//    /**
//     * @deprecated 2.2.3，旧版电力分析缺陷识别图片下载，已废弃将在后续版本删除
//     */
//    @Deprecated
//    void downloadPhoto(Integer recordType, String photoIds, HttpServletResponse response);

    /**
     * 表计的阈值
     *
     * @return
     */
    Map getThreshold();

    RestRes setThreshold(String value);

//    /**
//     * @deprecated 2.2.3，旧版缺陷识别数据导出，已废弃，将在后续版本删除
//     */
//    @Deprecated
//    void downloadDefect(String idList, HttpServletResponse response, HttpServletRequest request);

    RestRes getDefectMonthList(DefectMonthListReqDto defectMonthListReqDto);

//    /**
//     * @deprecated 2.2.3，旧版缺陷识别数据导出，已废弃，将在后续版本删除
//     */
//    @Deprecated
//    void downloadDefectInspection(String idList, HttpServletResponse response, HttpServletRequest request);

    List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime);

    List<Map> getInspect(List<Integer> missionRecordsIds);

    List<StationDefectPhotoDTO> getAllStationDefectPhotoDTO(Integer tagId, Integer type, String startTime, String endTime, Integer flag);

    /**
     * 获取照片、标签、问题来源等信息
     *
     * @param problemStatus
     * @param startTime
     * @param endTime
     * @param problemSourceList
     * @param tagId
     * @return
     */
    List<PhotoTagSourceDTO> getPhotoTagSourceList(Integer problemStatus, String startTime, String endTime, List<Integer> problemSourceList, Integer tagId);

    List<MissionPhotoEntity> getAllPhotoByMissionRecordsId(Integer missionRecordsId);

    List<StationDefectPhotoDTO> getAllStationDefectPhotoDTO1(Integer type, Long id, String startTime, String endTime, Integer defectStatus);

    RestRes uploadSrt(String execMissionID, MultipartFile fileData);

    RestRes checkNestMediaStatus(String nestId);

    RestRes tranDataCheck(String nestId,Integer uavWhich);

    void dealUploadMediaCallback(Boolean isSuccess, BaseResult3 baseResult3, Integer recordId, Integer missionId, String nestUuid,String env);

    /**
     * 根据照片Index范围找到对应的照片数据
     * @param indexFrom
     * @param indexTo
     * @param missionRecordsId
     * @return
     */
    List<MissionPhotoPointOutDTO> getPhotoByPoint(Integer indexFrom , Integer indexTo , String missionRecordsId);

    /**
     * 批量获取照片
     */
    List<MissionPhotoEntity> selectBatchPhoto(List<Long> photoIds);

    /**
     * 数据同步
     * @param nestId
     * @param recordIdList
     * @return
     */
    RestRes synDataList(String nestId, List<Integer> recordIdList);

    /**
     *
     * @param recordId
     * @return
     */
    List<Long> getPhotoByRecordId(Integer recordId);

    /**
     * 卸载电池
     * @param nestUuid
     */
    void closeNest(String nestUuid);

    /**
     * 发送WS消息
     *
     * @param uuid    uuid
     * @param message 消息
     */
    void pushPhotoTransMsgByWs(String uuid, String message);

    /**
     * 御三获取全景照片分页
     * @param startIndex
     * @param endIndex
     * @param missionRecordsId
     * @param type
     * @return
     */

    void photoAndGridRel(TaskEntity taskEntity, Integer missionRecordId);
}