package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.MissionVideoEntity;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.dto.in.RecordTaskVideoInDTO;

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
 * @since 2020-08-14
 */
public interface MissionVideoService extends IService<MissionVideoEntity> {

    /**
     * 通过架次执行id获取视频信息
     *
     * @param missionRecordId
     * @return
     */
    PageUtils getVideoPage(Map<String, Object> page, Integer missionRecordId);


    /**
     * 移动终端-创建录像
     *
     * @param missionId
     * @param missionRecordId
     * @param deviceId
     */
    void appCreateVideo(Integer missionId, Integer missionRecordId, String deviceId);

    /**
     * 移动终端-结束录像
     *
     * @param missionRecordId
     * @return
     */
    void appFinishVideo(Integer missionRecordId);


    /**
     * 删除视频
     *
     * @param missionRecordIds
     * @return
     */
    void deleteVideo(List<Integer> missionRecordIds);

    /**
     * 下载视频
     *
     * @param ids          要下载的视频idList
     * @param isAll           是否选中全部视频下载
     * @param missionRecordsId 架次执行id
     * @return
     */
    void downlandVideo(String ids, boolean isAll, Integer missionRecordsId, HttpServletResponse response);

    /**
     * 通过recordId获取fileIdList
     */
    List<String> getFileIdListByRecordId(Integer recordId);

    /**
     * 获取视频数量
     *
     * @param recordId
     * @return
     */
    Integer getVideoNum(Integer recordId);

    /**
     * 修改视频名称
     *
     * @param id
     * @param videoName
     * @return
     */
    RestRes updateVideoName(Long id, String videoName);

    /**
     * 创建relay任务
     * @param mode 重连模式 0-永久自动重连 1-心跳保持型(用创建后的id查询流状态保活) 2-输入输出没有错误时自动重连 3-输入流结束后停止
     * @param inUrl 自定义取流地址
     * @param outUrl 自定义推流地址
     * @param disableAudio 是否关闭音频
     * @param host 请求ip:port
     * @param comment 描述
     * @return
     */
    RestRes createRelay(Integer mode, String inUrl, String outUrl, Boolean disableAudio, String host, String comment);

    List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime);

    List<Map> getInspect(List<Integer> missionRecordsIds);

    /**
     * 更新架次下最新飞行轨迹
     * @param videoId
     */
    void updateMissionVideoLatLng(Integer videoId);

    void delSrt(Integer videoId);

    Long getIdByRecordId(Integer recordId);

    MissionVideoEntity getVideoByRecordId(Integer recordId);

    void getInspectLatLng(Long videoId);

    String getInspectLatLng(MissionVideoEntity missionVideo);

//    /**
//     * @deprecated 2.3.2，将在后续版本删除
//     */
//    @Deprecated
//    void checkCaptureStop(Integer missionId, String execId, String nestId,Integer uavWhich);

//    /**
//     * @deprecated 2.3.2，将在后续版本删除
//     */
//    @Deprecated
//    void createVideoCapture(String appName, String token, String host, String nestId, Integer missionId, Integer id, String execMissionID);

    /**
     * 保存视频GPS位置信息文件
     * @param videoId   视频ID
     * @param srtFilename   飞行轨迹文件名称
     * @param gpsJsonStorageUrl gps位置信息文件存储地址
     * @return  srt存储记录ID
     */
    String saveVideoGpsInfo(Integer videoId, String srtFilename, String gpsJsonStorageUrl);

    /**
     * 保存任务录像视频文件信息，该信息不完整
     * @param recordTaskId  录像任务ID
     * @param execMissionId CPS返还的执行任务ID
     * @return 录像视频ID
     */
    Long saveMissionRecordVideo(String recordTaskId, String execMissionId);

    /**
     * 保存任务录像视频文件信息，该信息不完整
     * @param recordTaskVideoInDTO  录像视频信息
     * @return 录像视频ID
     */
    void saveMissionRecordVideo(RecordTaskVideoInDTO recordTaskVideoInDTO);

    /**
     * 根据执行ID获取录像任务ID
     * @param execMissionId CPS返还的执行任务ID
     * @return  录像任务ID
     */
    String findRecordingTaskIdByExecId(String execMissionId);
    int selectNum(Integer videoId, String orgCode);
}
