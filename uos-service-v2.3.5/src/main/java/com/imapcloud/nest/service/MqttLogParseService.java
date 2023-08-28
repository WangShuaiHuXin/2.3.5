package com.imapcloud.nest.service;

import com.imapcloud.nest.pojo.dto.ParseEmqxLogDto;
import com.imapcloud.nest.pojo.dto.QueryMqttLogFromMongoDto;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.mongo.pojo.MqttLogEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface MqttLogParseService {

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    RestRes lsSpringLog();

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    RestRes parseSpringLog(List<String> pathList);

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    RestRes lsCpsLog(String nestId);

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    RestRes parseCpsLog(String nestId, List<String> pathList);

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    RestRes parseEmqxLog(ParseEmqxLogDto dto);

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    RestRes parseEmqxLogAsync(ParseEmqxLogDto dto);

    /**
     * @deprecated 2.2.3，使用文件服务上传接口替换上传，将在后续版本删除
     * @see NestLogService#saveNestLogStorageInfo(com.imapcloud.nest.v2.service.dto.in.CpsSyncNestLogInDTO)
     */
    @Deprecated
    RestRes uploadAndSaveLogZip(String nestUuid, MultipartFile logFile);

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    RestRes listMqttLogsFromMongo(QueryMqttLogFromMongoDto dto);

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    RestRes clearMqttLogs();

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    List<MqttLogEntity> listMqttLogsByTimestamp(Long startTime, Long endTime);

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    void exportMqttLogs(Long startTime, Long endTime, HttpServletResponse response);
}
