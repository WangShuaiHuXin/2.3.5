package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.enums.MinioEventTypeEnum;

/**
 * minio服务
 *
 * @author boluo
 * @date 2022-10-26
 */
public interface MinioService {

    /**
     * mission_photo表中文件设置tag
     *
     * @param tagVersion 标记版本
     * @return int 返回更新了多少数据
     */
    int missionPhoto(int tagVersion);

    /**
     * 解析文件事件
     *
     * @return int 返回更新了多少数据
     */
    int analysisFileEvent();

    /**
     * 分析tag标识，写入file_detail_info
     *
     * @param eventType 事件类型 1 删除  2 tag
     * @return int
     */
    int analysisEvent(MinioEventTypeEnum eventType);

    /**
     * 报告
     *
     * @param tagVersion 标记版本
     * @return int
     */
    int report(int tagVersion);

    /**
     * mission_video表中文件设置tag
     *
     * @param tagVersion 标记版本
     * @return int
     */
    int missionVideo(int tagVersion);

    /**
     * mission_video_photo表中文件设置tag
     *
     * @param tagVersion 标记版本
     * @return int
     */
    int missionVideoPhoto(int tagVersion);

    /**
     * 物理删除照片
     *
     * @param tagVersion 标记版本
     * @return int
     */
    int physicsDeleteMissionPhoto(int tagVersion);

    /**
     * 物理删除视频
     *
     * @param tagVersion 标记版本
     * @return int
     */
    int physicsDeleteMissionVideo(int tagVersion);

    /**
     * 物理删除视频照片
     *
     * @param tagVersion 标记版本
     * @return int
     */
    int physicsDeleteMissionVideoPhoto(int tagVersion);

    /**
     * 发送钉钉消息 存储空间使用情况
     *
     * @param tagVersion 标记版本
     */
    void send(int tagVersion);

    /**
     * 删除文件事件
     */
    void deleteFileEvent();
}
