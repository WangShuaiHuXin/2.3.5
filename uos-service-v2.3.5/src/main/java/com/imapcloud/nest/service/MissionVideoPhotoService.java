package com.imapcloud.nest.service;

import com.imapcloud.nest.model.MissionVideoPhotoEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.impl.FileCallbackHandleServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视屏抽帧的图片 服务类
 * </p>
 *
 * @author hc
 * @since 2021-06-30
 */
public interface MissionVideoPhotoService extends IService<MissionVideoPhotoEntity> {

    /**
     * 处理抽帧结果
     * @param videoFrameResult 抽帧结果
     */
    void handleExtractingResult(FileCallbackHandleServiceImpl.VideoFrameResult videoFrameResult);

    /**
     * 抽帧提取照片
     *
     * @param id          id
     * @param seconds     秒
     * @param extractTime 提取时间
     * @param videoName   视频名字
     * @return boolean
     */
    boolean extractPhoto(Integer id,Integer seconds, String extractTime,String videoName);

    List<Map> getAllPhotos(Integer missionRecordsId);

    void cancel(Integer id, String extractTime);

    boolean del(List<Integer> ids);

    void downlandPic(String ids, HttpServletResponse response);

    boolean extractSrt(Integer videoId);

    /**
     * 删除视频抽帧照片
     *
     * @param missionRecordIds 任务记录id
     */
    void deleteVideoPhoto(List<Integer> missionRecordIds);

    /**
     * 查找下载的图片数据
     * @param ids
     * @param orgCode
     * @return
     */
    int selectNum(String[] ids, String orgCode);
}
