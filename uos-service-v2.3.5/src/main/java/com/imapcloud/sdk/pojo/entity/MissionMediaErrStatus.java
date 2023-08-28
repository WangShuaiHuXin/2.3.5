package com.imapcloud.sdk.pojo.entity;

import lombok.Data;

@Data
public class MissionMediaErrStatus {
    /**
     * START_UP（自动开机阶段）
     * MATCH_MISSION（任务匹配阶段）
     * MATCH_FILE（媒体文件匹配阶段）
     * INIT_MANAGER（媒体管理器初始化阶段）
     * ENTER_DOWNLOAD_MODE（切换下载模式阶段）
     * REFRESH_FILE_LIST（刷新无人机文件列表阶段）
     * DOWNLOAD_FILE（媒体文件下载阶段）
     * UPLOAD_FILE（媒体文件上传阶段）
     * UPLOAD_FILE_BY_CHUNK_INIT（分片上传初始化阶段）
     * UPLOAD_FILE_BY_CHUNK_POST（分片数据上传阶段）
     * UPLOAD_FILE_BY_CHUNK_COMBINE（分片数据合并阶段）
     * UPLOAD_FILE_BY_CHUNK_SYNC（分片数据合并后的文件信息同步阶段）
     */
    public String errorStep;
    /**
     * 4000：媒体文件不存在
     * 4001：媒体文件匹配不到
     * 4002：飞行任务匹配不到
     * 4010：媒体管理器初始化失败
     * 4011：切换到下载模式失败
     * 4012：文件列表刷新失败
     * 4020：SDK报错
     * 4021：文件未下载
     * 4022：无人机不在线
     * 4023：无人机自动开机失败
     * 4030：网络错误
     * 4031：服务器错误
     * 0：未知错误
     */
    public String errorCode;
    public String errorFile;
    public String errorInfo;
    public Integer httpCode;
    /**
     * 错误处理建议
     */
    public String errorSolution;
}
